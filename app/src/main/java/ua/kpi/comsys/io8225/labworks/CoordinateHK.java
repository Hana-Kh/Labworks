package ua.kpi.comsys.io8225.labworks;

public class CoordinateHK {
    enum Direction{
        LATITUDE,
        LONGITUDE
    }

    public Direction direction;
    public int degree;
    public int minute;
    public int second;

    public CoordinateHK(){
        degree = 0;
        minute = 0;
        second = 0;
        direction = Direction.LATITUDE;
    }

    public CoordinateHK(int degree, int minute, int second, Direction direction){
        this.direction = direction;
        if (direction == Direction.LATITUDE){
            if (degree >= -90 && degree <= 90){
                this.degree = degree;
            }
            else{
                System.err.println("Incorrect degree with current Direction " + degree);
                throw new ArithmeticException();
            }
        }
        else {
            if (degree >= -180 && degree <= 180){
                this.degree = degree;
            }
            else{
                System.err.println("Incorrect degree with current Direction " + degree);
                throw new ArithmeticException();
            }
        }
        if (minute >= 0 && minute <= 59){
            this.minute = minute;
        }
        else {
            System.err.println("Incorrect minute "+minute);
            throw new ArithmeticException();
        }

        if (second >= 0 && second <= 59){
            this.second = second;
        }
        else {
            System.err.println("Incorrect second "+second);
            throw new ArithmeticException();
        }
    }

    public String getWorldPart(int degree, Direction direction){
        if (direction == Direction.LATITUDE){
            if (degree >= 0)
                return"N";
            else
               return "S";
        }
        else {
            if (degree >= 0)
               return "E";
            else
               return "W";
        }
    }

    public String getInt(){
        return String.format("%d°%d'%d\" %s", Math.abs(degree), minute, second, getWorldPart(degree, direction));
    }

    public String getFloat(){
        int koef;
        if (degree >= 0)
            koef = 1;
        else
            koef = -1;

        float floatCoord = (Math.abs(degree) + (float)minute/60 + (float)second/3600);
        return String.format("%f° %s", floatCoord * koef, getWorldPart(degree, direction));
    }

    public CoordinateHK getMiddle(CoordinateHK a, CoordinateHK b) {
        if (a.direction == b.direction){
            int midDegree = (a.degree + b.degree) / 2;
            int midMinute = (a.minute + b.minute) / 2;
            int midSecond = (a.second + b.second) / 2;
            return new CoordinateHK(midDegree, midMinute, midSecond, a.direction);
        }
        else {
            return null;
        }
    }

    public CoordinateHK getMiddle(CoordinateHK a) {
        return getMiddle(this, a);
    }

}
