package be.ida.jetpack.advanceddroptargets.helper;

import org.apache.sling.api.request.RequestParameter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MockRequestParameter implements RequestParameter {

    private final String value;
    private String name;
    private String encoding = "UTF-8";
    private byte[] content;

    public MockRequestParameter(String value) {
        this.value = value;
        content = null;
    }

    @Override
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public byte[] get() {
        if (content == null) {
            try {
                content = getString().getBytes(getEncoding());
            } catch (Exception e) {
                // UnsupportedEncodingException, IllegalArgumentException
                content = getString().getBytes();
            }
        }
        return content;
    }

    @Override
    public String getContentType() {
        // none known for www-form-encoded parameters
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(get());
    }

    @Override
    public String getFileName() {
        // no original file name
        return null;
    }

    @Override
    public long getSize() {
        return get().length;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(get(), encoding);
    }

    @Override
    public boolean isFormField() {
        // www-form-encoded are always form fields
        return true;
    }

    @Override
    public String toString() {
        return getString();
    }

}