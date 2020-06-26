package com.example.deqodetask.Model;


//Model Class to store all the information Regarding the Paraleel task
public class ProgressModel {
    String operation = "Operation:" ;
    String message = "Message:" ;
    int progress = 0;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ProgressModel(String operation, String message , int progress){
      this.message = message;
      this.operation = operation;
      this.progress = progress;
    }


}
