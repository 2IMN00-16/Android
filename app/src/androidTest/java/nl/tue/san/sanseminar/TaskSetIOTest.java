package nl.tue.san.sanseminar;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import nl.tue.san.sanseminar.components.tasks.Task;
import nl.tue.san.sanseminar.components.tasks.TaskSet;
import nl.tue.san.sanseminar.components.tasks.TaskSetIO;

/**
 * Created by Maurice on 6-1-2017.
 */

public class TaskSetIOTest {


    private final Task t1 = Task.createImmediateTask("T1", 0xFFFF0000,10,5,2,5,5);
    private final Task t2 = Task.createImmediateTaskWithoutThreshold("T2", 0xFFFF00FF,10,5,2,5);

    private final TaskSet ts12 = new TaskSet("t1t2",t1,t2);
    private final TaskSet ts21 = new TaskSet("t2t1",t2,t1);
    private final TaskSet ts1 = new TaskSet("t1",t1);

    /**
     * Tests that we can translate a TaskSet to JSON and back without losing information.
     * @throws JSONException If something goes wrong when translating.
     */
    @Test
    public void testTranslationLoses() throws JSONException {
        Assert.assertEquals(ts12, TaskSetIO.fromJSON(TaskSetIO.toJSON(ts12)));
        Assert.assertEquals(ts1, TaskSetIO.fromJSON(TaskSetIO.toJSON(ts1)));
        Assert.assertEquals(ts21, TaskSetIO.fromJSON(TaskSetIO.toJSON(ts21)));

        Assert.assertNotEquals(ts1, TaskSetIO.fromJSON(TaskSetIO.toJSON(ts12)));
    }
}
