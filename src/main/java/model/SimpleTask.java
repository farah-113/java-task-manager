package model;

public final class SimpleTask extends Task {

    private int startHour;
    private int endHour;

    public SimpleTask(int idTask, int startHour, int endHour) {
        super(idTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {
        int duration = endHour - startHour;
        if (duration < 0) {
            duration += 24;
        }

        return duration;
    }
}
