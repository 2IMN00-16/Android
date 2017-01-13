package nl.tue.san.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Maurice on 5-1-2017.
 *
 * This object uses a single ReadWriteLock to allow read and write operations to occur. To use these
 * locks, call {@link nl.tue.san.util.ReadWriteSafeObject#readOp(Operation)} or
 * {@link nl.tue.san.util.ReadWriteSafeObject#writeOp(Operation)}, providing an
 * Operation that should be executed when the lock is obtained. Not that multiple reads may occur
 * simultaneously, but a combination of reads and writes, or multiple writes, may not.
 */
public class ReadWriteSafeObject {

    private final java.util.concurrent.locks.ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Runs the given operation using a read lock in the current thread. This means that before the
     * operation is performed, the read lock is acquired. Then when the operation is performed the
     * read lock is released. In case of exceptions the lock is released and the exception is
     * rethrown.
     * @param operation The operation to execute
     * @param <T> The return type of the Operation.
     * @return The result of running the operation.
     */
    public <T> T readOp (Operation<T> operation){
        return this.lockOp(operation, this.lock.readLock());
    }

    /**
     * Runs the given operation using a write lock in the current thread. This means that before the
     * operation is performed, the write lock is acquired. Then when the operation is performed the
     * write lock is released. In case of exceptions the lock is released and the exception is
     * rethrown.
     * @param operation The operation to execute
     * @param <T> The return type of the Operation.
     * @return The result of running the operation.
     */
    public <T> T writeOp (Operation<T> operation){
        return this.lockOp(operation, this.lock.writeLock());
    }

    /**
     * Perform the given operation under the given lock. This will acquire the lock before performing the operation, and releases it when its done performing. If  an exception occurs while
     * @param operation
     * @param lock
     * @param <T>
     * @return
     */
    public <T> T lockOp(Operation<T> operation, Lock lock){
        try {
            lock.lock();
            return operation.perform();
        } finally {
            lock.unlock();
        }

    }
    /**
     * Simple interface to allow for the definition of arbitrary operations, while being Java 7 compliant.
     * @param <T> The return type of the operation.
     */
    public interface Operation<T> {
        T perform();
    }
}
