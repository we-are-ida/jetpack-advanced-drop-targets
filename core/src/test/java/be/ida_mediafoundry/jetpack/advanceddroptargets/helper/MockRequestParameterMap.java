package be.ida_mediafoundry.jetpack.advanceddroptargets.helper;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MockRequestParameterMap implements RequestParameterMap {

    private final Map<String, RequestParameter[]> delegate = new HashMap<>();

    @Override
    public RequestParameter getValue(String name) {
        RequestParameter[] params = getValues(name);
        return (params != null && params.length > 0) ? params[0] : null;
    }

    @Override
    public RequestParameter[] getValues(String name) {
        return delegate.get(name);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public RequestParameter[] get(Object key) {
        return delegate.get(key);
    }

    @Override
    public RequestParameter[] put(String key, RequestParameter[] value) {
        return delegate.put(key, value);
    }

    @Override
    public RequestParameter[] remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends RequestParameter[]> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<RequestParameter[]> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, RequestParameter[]>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

}