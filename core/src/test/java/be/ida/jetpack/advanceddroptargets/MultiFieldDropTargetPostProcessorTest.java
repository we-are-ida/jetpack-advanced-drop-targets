package be.ida.jetpack.advanceddroptargets;

import be.ida.jetpack.advanceddroptargets.helper.MockRequestParameter;
import be.ida.jetpack.advanceddroptargets.helper.MockRequestParameterMap;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.post.Modification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MultiFieldDropTargetPostProcessorTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.load().json("/mocks/drop-target-post-processor.json", "/content/postprocessor");
    }

    @Test
    public void given_validDropTargetAndExistingMultiValue_expect_valueAppended() throws Exception {
        RequestParameterMap requestParameterMap = new MockRequestParameterMap();
        requestParameterMap.put("./dropTarget->@items", new RequestParameter[]{new MockRequestParameter("b")});

        Resource resource = context.resourceResolver().getResource("/content/postprocessor/jcr:content/existingMultiValue");
        SlingHttpServletRequest mockRequest = mock(SlingHttpServletRequest.class);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getRequestParameterMap()).thenReturn(requestParameterMap);
        MultiFieldDropTargetPostProcessor dropTargetPostProcessor = new MultiFieldDropTargetPostProcessor();
        List<Modification> modifications = new ArrayList<>();

        dropTargetPostProcessor.process(mockRequest, modifications);

        assertThat(modifications)
            .isNotEmpty()
            .hasSize(1);
        assertThat(mockRequest.getResource()).isNotNull();

        assertThat(mockRequest.getResource().getValueMap())
            .isNotEmpty()
            .containsKeys("./items")
            .doesNotContainKeys("./dropTarget->@items");

        assertThat(mockRequest.getResource().getValueMap().get("./items", String[].class))
            .isNotEmpty()
            .containsExactly("a", "b");
    }

    @Test
    public void given_validDropTargetAndNoExistingMultiValue_WithSubNode_expect_valueAppended() throws Exception {
        RequestParameterMap requestParameterMap = new MockRequestParameterMap();
        requestParameterMap.put("./dropTarget->/subNode/@items", new RequestParameter[]{new MockRequestParameter("b")});

        Resource resource = context.resourceResolver().getResource("/content/postprocessor/jcr:content/noExistingMultiValue");
        SlingHttpServletRequest mockRequest = mock(SlingHttpServletRequest.class);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getRequestParameterMap()).thenReturn(requestParameterMap);
        MultiFieldDropTargetPostProcessor dropTargetPostProcessor = new MultiFieldDropTargetPostProcessor();
        List<Modification> modifications = new ArrayList<>();

        dropTargetPostProcessor.process(mockRequest, modifications);

        assertThat(modifications)
            .isNotEmpty()
            .hasSize(1);
        assertThat(mockRequest.getResource()).isNotNull();

        assertThat(mockRequest.getResource().getChild("dropTarget->"))
            .isNull();

        assertThat(mockRequest.getResource().getChild("subNode"))
            .isNotNull();

        assertThat(mockRequest.getResource().getValueMap())
            .isNotEmpty()
            .doesNotContainKeys("./dropTarget->/subNode/@items");

        assertThat(mockRequest.getResource().getChild("subNode").getValueMap().get("./items", String[].class))
            .isNotEmpty()
            .containsExactly("b");
    }

    @Test
    public void given_validDropTargetAndNonExistingMultiValue_expect_valueCreated() throws Exception {
        RequestParameterMap requestParameterMap = new MockRequestParameterMap();
        requestParameterMap.put("./dropTarget->@items", new RequestParameter[]{new MockRequestParameter("b")});

        Resource resource = context.resourceResolver().getResource("/content/postprocessor/jcr:content/noExistingMultiValue");
        SlingHttpServletRequest mockRequest = mock(SlingHttpServletRequest.class);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getRequestParameterMap()).thenReturn(requestParameterMap);
        MultiFieldDropTargetPostProcessor dropTargetPostProcessor = new MultiFieldDropTargetPostProcessor();
        List<Modification> modifications = new ArrayList<>();

        dropTargetPostProcessor.process(mockRequest, modifications);

        assertThat(modifications).isNotEmpty().hasSize(1);
        assertThat(mockRequest.getResource()).isNotNull();

        assertThat(mockRequest.getResource().getValueMap())
            .isNotEmpty()
            .containsKeys("./items")
            .doesNotContainKeys("./dropTarget->@items");

        assertThat(mockRequest.getResource().getValueMap().get("./items", String[].class))
            .isNotEmpty()
            .containsExactly("b");
    }

    @Test
    public void given_validDropTargetAndExistingSingleValue_expect_valueAddedAndConvertedToMultifield() throws Exception {
        RequestParameterMap requestParameterMap = new MockRequestParameterMap();
        requestParameterMap.put("./dropTarget->@items", new RequestParameter[]{new MockRequestParameter("b")});

        Resource resource = context.resourceResolver().getResource("/content/postprocessor/jcr:content/existingSingleValue");
        SlingHttpServletRequest mockRequest = mock(SlingHttpServletRequest.class);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getRequestParameterMap()).thenReturn(requestParameterMap);
        MultiFieldDropTargetPostProcessor dropTargetPostProcessor = new MultiFieldDropTargetPostProcessor();
        List<Modification> modifications = new ArrayList<>();

        dropTargetPostProcessor.process(mockRequest, modifications);

        assertThat(modifications)
            .isNotEmpty()
            .hasSize(1);
        assertThat(mockRequest.getResource()).isNotNull();

        assertThat(mockRequest.getResource().getValueMap())
            .isNotEmpty()
            .containsKeys("./items")
            .doesNotContainKeys("./dropTarget->@items");

        assertThat(mockRequest.getResource().getValueMap().get("./items", String[].class))
            .isNotEmpty()
            .containsExactly("a", "b");
    }

    @Test
    public void given_validDropTargetAndNoExistingComposite_expect_node_created() throws Exception {
        RequestParameterMap requestParameterMap = new MockRequestParameterMap();
        requestParameterMap.put("./dropTarget->/subNode/{{COMPOSITE}}/@link", new RequestParameter[]{new MockRequestParameter("b")});

        Resource resource = context.resourceResolver().getResource("/content/postprocessor/jcr:content/existingComposite");
        SlingHttpServletRequest mockRequest = mock(SlingHttpServletRequest.class);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getRequestParameterMap()).thenReturn(requestParameterMap);
        MultiFieldDropTargetPostProcessor dropTargetPostProcessor = new MultiFieldDropTargetPostProcessor();
        List<Modification> modifications = new ArrayList<>();

        dropTargetPostProcessor.process(mockRequest, modifications);

        assertThat(modifications)
            .isNotEmpty()
            .hasSize(1);
        assertThat(mockRequest.getResource()).isNotNull();

        assertThat(mockRequest.getResource().getValueMap())
            .isNotEmpty()
            .doesNotContainKeys("./items")
            .doesNotContainKeys("./dropTarget->/subNode/{{COMPOSITE}}/@link");

        assertThat(mockRequest.getResource().getChild("dropTarget->"))
            .isNull();

        assertThat(mockRequest.getResource().getChild("subNode"))
            .isNotNull();

        assertThat(mockRequest.getResource().getChild("subNode").getChildren())
            .hasSize(3);

        assertThat(mockRequest.getResource().getChild("subNode/item0"))
            .isNotNull();

        assertThat(mockRequest.getResource().getChild("subNode/item1"))
            .isNotNull();

        assertThat(mockRequest.getResource().getChild("subNode/item2"))
            .isNotNull();

        assertThat(mockRequest.getResource().getChild("subNode/item0").getValueMap())
            .contains(
                entry("jcr:primaryType", "nt:unstructured"),
                entry("link", "a")
            );

        assertThat(mockRequest.getResource().getChild("subNode/item1").getValueMap())
            .contains(
                entry("jcr:primaryType", "nt:unstructured"),
                entry("link", "c")
            );

        assertThat(mockRequest.getResource().getChild("subNode/item2").getValueMap())
            .contains(
                entry("link", "b")
            );
    }

}