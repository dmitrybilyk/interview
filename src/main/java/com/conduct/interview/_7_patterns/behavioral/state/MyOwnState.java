package com.conduct.interview._7_patterns.behavioral.state;


import lombok.Getter;
import lombok.Setter;

public class MyOwnState {
    public static void main(String[] args) {
        MyActivity activity = new MyActivity();

        activity.doSomething();
        activity.doSomething();
        activity.doSomething();
        activity.doSomething();
        activity.doSomething();
        activity.doSomething();

    }
}

//State
interface MyState {
    void doActivity();
}

//Concreate State
class WorkingState implements MyState {
    private final MyActivity myActivity;

    public WorkingState(MyActivity myActivity) {
        this.myActivity = myActivity;
    }

    @Override
    public void doActivity() {
        System.out.println("I'm working now");
        myActivity.changeActivity(myActivity.getRestState());
    }
}

//Concreate State
class RestState implements MyState {
    private final MyActivity myActivity;

    public RestState(MyActivity myActivity) {
        this.myActivity = myActivity;
    }

    @Override
    public void doActivity() {
        System.out.println("I'm having a rest now");
        myActivity.changeActivity(myActivity.getWalkState());
    }
}

//Concreate State
class WalkState implements MyState {
    private final MyActivity myActivity;

    public WalkState(MyActivity myActivity) {
        this.myActivity = myActivity;
    }

    @Override
    public void doActivity() {
        System.out.println("I'm walking now");
        myActivity.changeActivity(myActivity.getWorkingState());
    }
}

//Context
@Getter
@Setter
class MyActivity {
    private MyState workingState;
    private MyState restState;
    private MyState walkState;
    private MyState currentState;

    MyActivity() {
        this.workingState = new WorkingState(this);
        this.restState = new RestState(this);
        this.walkState = new WalkState(this);
        this.currentState = restState;
    }

    void changeActivity(MyState state) {
        this.currentState = state;
    }

    public void doSomething() {
        this.currentState.doActivity();
    }
}