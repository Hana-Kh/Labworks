package ua.kpi.comsys.io8225.labworks;

public class Lab1_2_2 {
    public static void main(String[] args) {
        CoordinateHK coordinateX = new CoordinateHK();
        CoordinateHK coordinateY = new CoordinateHK(12, 13, 14, CoordinateHK.Direction.LONGITUDE);
        CoordinateHK coordinateZ = new CoordinateHK(-67, 24, 43, CoordinateHK.Direction.LATITUDE);
        CoordinateHK coordinateA = new CoordinateHK(160, 34, 35, CoordinateHK.Direction.LONGITUDE);

        System.out.println(coordinateA.getFloat());

        System.out.println(coordinateX.getMiddle(coordinateZ).getInt());
        System.out.println(coordinateY.getMiddle(coordinateX, coordinateZ).getFloat());
    }
}
