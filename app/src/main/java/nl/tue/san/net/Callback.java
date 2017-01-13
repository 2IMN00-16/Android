package nl.tue.san.net;

public interface Callback {
    void onSuccess(String data);
    void onFailure();
}
