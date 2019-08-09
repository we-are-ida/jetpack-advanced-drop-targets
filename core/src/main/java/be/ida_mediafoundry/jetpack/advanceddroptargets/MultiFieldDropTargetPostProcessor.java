package be.ida_mediafoundry.jetpack.advanceddroptargets;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
    MultiFieldDropTargetPostProcessor

    This PostProcessor listens to all post servlet actions that contain a property that starts with DROPTARGET_PREFIX.
    It will take the value from the property and add it to the property that follows the DROPTARGET_PREFIX.

    f.e.

    <cq:dropTargets jcr:primaryType="nt:unstructured">
      <books
            jcr:primaryType="cq:DropTargetConfig"
            ...
            propertyName="./dropTarget->@books">
      </books>
    </cq:dropTargets>

    Here the PostProcessor will take the value from "./dropTarget->@books" and add it to the property "books"

    <cq:dropTargets jcr:primaryType="nt:unstructured">
      <books
            jcr:primaryType="cq:DropTargetConfig"
            ...
            propertyName="./dropTarget->/definitions/{{COMPOSITE}}/@link">
      </books>
    </cq:dropTargets>

    Here the PostProcessor will take the value and create the a property under a sub node "/definitions/item0" named "link"

*/

@Component(service = {SlingPostProcessor.class})
public class MultiFieldDropTargetPostProcessor implements SlingPostProcessor {

    private static final String DROP_TARGET_PREFIX = "./dropTarget->";
    private static final String COMPOSITE_VARIABLE = "{{COMPOSITE}}";
    private static final String PROPERTY_PREFIX = "@";
    private static final String SLASH = "/";
    private static final String SLING_PROPERTY_PREFIX = "./";

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> modifications) throws Exception {
        RequestParameterMap requestParameterMap = request.getRequestParameterMap();

        for (String key : requestParameterMap.keySet()) {
            if (key.startsWith(MultiFieldDropTargetPostProcessor.DROP_TARGET_PREFIX)) {

                RequestParameter requestParameter = requestParameterMap.getValue(key);
                if (requestParameter != null) {
                    String target = key.replace(MultiFieldDropTargetPostProcessor.DROP_TARGET_PREFIX, StringUtils.EMPTY);
                    String propertyValue = requestParameter.getString();
                    Resource resource = request.getResource();
                    processProperty(resource, target, propertyValue, key);
                    modifications.add(Modification.onModified(resource.getPath()));
                }
            }
        }
    }

    private void processProperty(Resource resource, String target, String propertyValue, String originalKey) throws Exception {
        String[] paths = target.split(SLASH);

        ResourceResolver resourceResolver = resource.getResourceResolver();

        //clean-up the dropTarget property or node
        ModifiableValueMap originalProperties = resource.adaptTo(ModifiableValueMap.class);
        originalProperties.remove(originalKey);

        String dropTargetNodeName = DROP_TARGET_PREFIX.replace(MultiFieldDropTargetPostProcessor.SLING_PROPERTY_PREFIX, StringUtils.EMPTY);
        Resource dropTargetResource = resource.getChild(dropTargetNodeName);
        if (dropTargetResource != null) {
            resourceResolver.delete(dropTargetResource);
        }

        //check all paths and create correct resources and properties
        boolean isArray = true;
        Resource currentResource = resource;
        for (String path : paths) {
            if (path.startsWith(PROPERTY_PREFIX)) {
                //this is the property
                String propertyName = path.replace(PROPERTY_PREFIX, StringUtils.EMPTY);
                ModifiableValueMap properties = currentResource.adaptTo(ModifiableValueMap.class);
                if (isArray) {
                    List<String> childPages = new ArrayList<>(Arrays.asList(properties.get(propertyName, new String[0])));
                    childPages.add(propertyValue);
                    properties.remove(propertyName);
                    properties.put(propertyName, childPages.toArray());
                } else {
                    properties.put(propertyName, propertyValue);
                }
            } else if (path.equals(COMPOSITE_VARIABLE)) {
                //create new subNode
                int count = Iterators.size(currentResource.getChildren().iterator());
                String nodeName = "item" + count;
                currentResource = resourceResolver.create(currentResource, nodeName, new HashMap<>());
                isArray = false;
            } else if (StringUtils.isNotBlank(path)) {
                //get or create new node
                Resource subResource = currentResource.getChild(path);
                if (subResource == null) {
                    currentResource = resourceResolver.create(currentResource, path, new HashMap<>());
                } else {
                    currentResource = subResource;
                }
            }
        }
    }
}