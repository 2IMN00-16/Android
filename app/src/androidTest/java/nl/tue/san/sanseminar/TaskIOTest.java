package nl.tue.san.sanseminar;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.tue.san.sanseminar.components.Task;
import nl.tue.san.sanseminar.components.TaskIO;

/**
 * Created by Maurice on 5-1-2017.
 *
 * Must run on Android as we use the JSON definition that is baked into Android.
 */
@RunWith(AndroidJUnit4.class)
public class TaskIOTest {

    private final Task t1 = Task.createImmediateTask("T1", 0xFFFF0000,10,5,2,5,5);
    private final Task t2 = Task.createImmediateTaskWithoutThreshold("T2", 0xFFFF00FF,10,5,2,5);
    private final Task t3 = Task.createTaskWithoutThreshold("T3", 0xFFFFFF00,10,5,2,5,5);
    private final Task t4 = new Task("T4", 0xFFFFFFFF,5,10,5,2,5,2);


    /**
     * Tests that we can translate a task to JSON and back without losing information.
     * @throws JSONException If something goes wrong when translating.
     */
    @Test
    public void testTranslationLoses() throws JSONException {
        Assert.assertEquals(t1, TaskIO.fromJSON(TaskIO.toJSON(t1)));
        Assert.assertEquals(t2, TaskIO.fromJSON(TaskIO.toJSON(t2)));
        Assert.assertEquals(t3, TaskIO.fromJSON(TaskIO.toJSON(t3)));
        Assert.assertEquals(t4, TaskIO.fromJSON(TaskIO.toJSON(t4)));
    }

    /**
     * Test that we can leave out certain properties in JSON without causing issues, as long as a task has default properties.
     */
    @Test
    public void testTranslationMisses() throws JSONException {

        // By default it should work.
        JSONObject t1Translation = TaskIO.toJSON(t1);
        Assert.assertEquals(t1, TaskIO.fromJSON(t1Translation));

        // When we remove offset it should still work
        t1Translation.remove("Offset");
        Assert.assertEquals(t1, TaskIO.fromJSON(t1Translation));


        // If we remove threshold then the translation is no longer accurate
        t1Translation.remove("Threshold");
        Assert.assertNotEquals(t1, t1Translation);


        // If we remove the name, then the translation should fail entirely.
        t1Translation.remove("Name");
        try {
            TaskIO.fromJSON(t1Translation);
            // We expect an exception
            Assert.fail();
        } catch (JSONException e){
            // The expected exception occurred, it should indicate that "Name" is absent.
            Assert.assertEquals("No value for Name", e.getMessage());
        }

    }



}
