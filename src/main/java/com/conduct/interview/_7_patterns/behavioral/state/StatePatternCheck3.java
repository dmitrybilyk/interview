package com.conduct.interview._7_patterns.behavioral.state;

import lombok.Getter;

public class StatePatternCheck3 {
    public static void main(String[] args) {
        MyActionContext myActionContext = new MyActionContext();
        myActionContext.setMyState(new MyRelaxState(myActionContext));
        myActionContext.doMyAction();
        myActionContext.doSomeActivity();
        myActionContext.doMyAction();
        myActionContext.doSomeActivity();
        myActionContext.doMyAction();
        myActionContext.setMyState(new MyStudyState(myActionContext));
        myActionContext.doMyAction();
        myActionContext.setMyState(new MyRelaxState(myActionContext));
        myActionContext.doMyAction();
        myActionContext.doMyAction();
        myActionContext.setMyState(new MyBookReadingState(myActionContext));
        myActionContext.doMyAction();
    }
}

@Getter
class MyActionContext {
    private MyState myState = new MyRelaxState(this);

    public void setMyState(MyState myState) {
        this.myState = myState;
    }
    public void doMyAction() {
        myState.doAction();
    }

    public void doSomeActivity() {
        if (this.myState.getLevelOfActivity() == MyState.LevelOfActivity.ACTIVE) {
            System.out.println("too much activity, relax");
            this.myState = new MyRelaxState(this);
        } else {
            myState = new MySportState(this);
        }
    }
}

@Getter
abstract class MyState {
    private LevelOfActivity levelOfActivity;
    public enum LevelOfActivity {
        ACTIVE,
        PASSIVE
    }
    protected MyActionContext myActionContext;
    public MyState(MyActionContext myActionContext, LevelOfActivity levelOfActivity) {
        this.myActionContext = myActionContext;
        this.levelOfActivity = levelOfActivity;
    }
    abstract void doAction();
}

class MySportState extends MyState {

    public MySportState(MyActionContext myActionContext) {
        super(myActionContext, LevelOfActivity.ACTIVE);
    }

    @Override
    public void doAction() {
        System.out.println("Doing sports");
    }
}

class MyStudyState extends MyState {

    public MyStudyState(MyActionContext myActionContext) {
        super(myActionContext, LevelOfActivity.PASSIVE);
    }

    @Override
    public void doAction() {
        System.out.println("Studying");
    }
}

class MyBookReadingState extends MyState {

    public MyBookReadingState(MyActionContext myActionContext) {
        super(myActionContext, LevelOfActivity.PASSIVE);
    }

    @Override
    public void doAction() {
        System.out.println("Reading a book");
    }
}

class MyRelaxState extends MyState {

    public MyRelaxState(MyActionContext myActionContext) {
        super(myActionContext, LevelOfActivity.PASSIVE);
    }

    @Override
    public void doAction() {
        System.out.println("Relaxing");
    }
}
