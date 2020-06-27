package com.example.deqodetask.Model;


//Model Class to store all the information Regarding the Paraleel task
public class ProgressModel {
    String operation = "Operation:" ;
    String message = "Message:" ;
    int progress = 0;
    int guid = 0;

    public ProgressModel(String operation, String message, int progress, int guid) {
        this.operation = operation;
        this.message = message;
        this.progress = progress;
        this.guid = guid;
    }

    public String getOperation() {
        return operation;
    }

    public String getMessage() {
        return message;
    }

    public int getProgress() {
        return progress;
    }

    public int getGuid() {
        return guid;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
