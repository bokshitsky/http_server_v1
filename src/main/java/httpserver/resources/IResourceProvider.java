package httpserver.resources;


public interface IResourceProvider {

    //The only required method:
    public byte[] getResource(String resourceName);

}
