package nl.tue.san.util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

/**
 * Created by Maurice on 13-1-2017.
 */

public abstract class Manager<T>  {

    private final ReadWriteSafeObject rwSemaphore = new ReadWriteSafeObject();
    private File file;

    /**
     * Creates a new Manager that doesn't have a File to read from/to write to yet.
     */
    protected Manager(){
    }

    /**
     * Creates a new Manager from the given file
     * @param file
     */
    protected Manager(File file){
        this();
        this.setFile(file);
        try {
            this.reload();
        } catch (Exception e) {
            Log.d(this.getClass().getName(),"Couldn't load visualization from file "+this.file.getAbsolutePath()+
                    " due to "+e.getClass().getSimpleName()+":  "+e.getMessage()+"." +
                    " Call reload on "+this.getClass().getName()+" to get full exception.");
        }

    }

    protected void setFile(final File file){
        this.writeOp(new ReadWriteSafeObject.Operation<Void>() {
            @Override
            public Void perform() {
                Manager.this.file = file;
                return null;
            }
        });
    }

    private T managed;

    /**
     * Convert the managed object to a String. This method and {@link #unmarshall(String)} must be
     * defined in such a way that {@code unmarshall(marshall(managed)).equals(managed)}. If this
     * does not hold, the marshalling is useless.
     * @param managed The managed object to marshall.
     * @return A marshalling of the managed object that can be unmarshalled to recreate the managed
     * object.
     * @throws Exception If anything went wrong during marshalling.
     */
    protected abstract String marshall(T managed) throws Exception;

    /**
     * Convert a String back to the managed object. This method and {@link #marshall(Object)} must
     * be defined in such a way that {@code unmarshall(marshall(managed)).equals(managed)}. If this
     * does not hold, the unmarshalling is useless.
     * @param content A String that was the result of calling marshall on the managed object.
     * @return The object that was described by the given string, which should be managed.
     * @throws Exception If anything went wrong during unmarshalling.
     */
    protected abstract T unmarshall(String content) throws Exception;

    /**
     * Asserts that access has been given to the required directory. If access was not given, an
     * {@link IllegalStateException} is thrown. If the assertion is met, the method terminates
     * normally.
     */
    private void assertAccess(){
        if(file == null)
            throw new IllegalStateException("Can't access file to write to");
    }

    /**
     * Perform a reload of all TaskSets.
     */
    public void reload() throws Exception{
        assertAccess();

        // Convert the root file into a String
        StringBuilder builder = new StringBuilder();
        try (Reader reader = new InputStreamReader(new FileInputStream(this.file))) {
            while (reader.ready())
                builder.append((char)reader.read());
        }

        // Then reload using that String
        this.reload(builder.toString());
    }

    /**
     * Reload the managed object directly from the given marshalled string.
     * @param marshalled String representing the managed object, that must be unmarshalled.
     * @throws Exception If something went wrong while unmarshalling.
     */
    protected void reload(final String marshalled) throws Exception {
        // Here we read from a file, but we update this object. Therefore we use writeOp and not
        // readOp.
        Exception exception = rwSemaphore.writeOp(new ReadWriteSafeObject.Operation<Exception>() {
            @Override
            public Exception perform() {
                try {
                    Manager.this.managed = unmarshall(marshalled);
                    return null;
                } catch (Exception e) {
                    return e;
                }
            }
        });

        if(exception != null)
            throw exception;
    }


    /**
     * Writes all TaskSets as a JSONArray to the root file. This does not provide any
     * synchronization. When calling, this should use external synchronization allowing it to read.
     * This method does not change any properties on the TaskSetManager.
     */
    private String writeAsReturn() throws Exception {


        // We do marshalling of the managed object. For that we need to read it.
        // We either return the managed object marshalled in a String, or we return an Exception
        Object returned = rwSemaphore.readOp(new ReadWriteSafeObject.Operation<Object>() {
            @Override
            public Object perform() {
                try {
                    return marshall(managed);
                } catch (Exception e) {
                    return e;
                }
            }
        });

        // If we returned a non-null value that is an Exception, then something must have gone wrong
        // while marshalling.
        if(returned != null && returned instanceof Exception)
            throw (Exception)returned;

        return (String)returned;
    }

    /**
     * Write all TaskSets. This provides synchronization.
     */
    public void write() throws Exception{
        assertAccess();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file)))) {
            writer.write(this.writeAsReturn());
        }
    }


    protected <ReturnType> ReturnType readOp(ReadWriteSafeObject.Operation<ReturnType> operation){
        return this.rwSemaphore.readOp(operation);
    }

    protected <ReturnType> ReturnType writeOp(ReadWriteSafeObject.Operation<ReturnType> operation){
        return this.rwSemaphore.writeOp(operation);
    }

    /**
     * Get a direct reference to the managed object
     * @return The Managed object
     */
    protected T managed(){
        return managed;
    }
}


