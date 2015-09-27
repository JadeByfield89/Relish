package relish.permoveo.com.relish.interfaces;

/**
 * Created by Roman on 23.07.15.
 */
public interface IRequestable {
    void completed(Object... params);

    void failed(Object... params);
}
