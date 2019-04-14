package coursera.algorithms.sort;

import java.util.Arrays;
import java.util.Comparator;

/************************************
 * BruteCollinearPoints.java
 *  examines 4 points at a time and checks whether they all lie on the same line segment,
 *  returning all such line segments.
 *  To check whether the 4 points p, q, r, and s are collinear,
 *  check whether the three slopes between p and q, between p and r, and between p and s are all equal.
 *
 *  Performance:
 * should have maximum time complexity of n^4, space complexity of n
 ************************************/
public class BruteCollinearPoints {
    private Point[] points;
    private LineSegment[] segments;
    private int segmentsIndex;

    public BruteCollinearPoints(Point[] points) {
        if (points == null || oneOfThePointsIsNull(points) || arePointsRepeated(points))
            throw new IllegalArgumentException();
        this.points = getPoints(points);
        initialize();
    }

    public int numberOfSegments() {
        return segmentsIndex;
    }

    /**
     * The method segments() should include each line segment containing 4 points exactly once.
     * If 4 points appear on a line segment in the order p→q→r→s,
     * then you should include either the line segment p→s or s→p (but not both) and you should not include
     * subsegments such as p→r or q→r.
     * For simplicity, we will not supply any input to BruteCollinearPoints that has 5 or more collinear points.
     **/
    public LineSegment[] segments() {
        LineSegment[] segmentClone;
        segmentClone = new LineSegment[segmentsIndex];

        for (int i = 0; i < segmentsIndex; i++)
            segmentClone[i] = segments[i];
        return segmentClone;
    }

    private boolean oneOfThePointsIsNull(Point[] points) {
        for (Point point : points) {
            if (point == null)
                return true;
        }
        return false;
    }

    private boolean arePointsRepeated(Point[] points) {
        // brute or sorted check?
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0)
                    return true;
            }
        }
        return false;
    }

    private Point[] getPoints(Point[] points) {
        Point[] pointsClone = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            pointsClone[i] = points[i];
        return pointsClone;
    }

    private void initialize() {
        this.segments = new LineSegment[0];
        this.segmentsIndex = 0;
        computeSegments();
    }

    private void computeSegments() {
        Point[] pointsCopy = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            pointsCopy[i] = points[i];

        for (int i = 0; i < points.length; i++) {
            Arrays.sort(pointsCopy, customSlopeOrder(points[i]));
            for (int j = 2; j < points.length; j++)
                if (mainPointIsNotAmongComparingPoints(i, j) && isSlopeOf4PointsSame(pointsCopy, j))
                    addSegment(pointsCopy, j);
        }
    }

    private Comparator<Point> customSlopeOrder(Point p) {
        return (Point p1, Point p2) -> {
            Double slope1 = p.slopeTo(p1);
            Double slope2 = p.slopeTo(p2);
            if (pointsInComparisonAreInHorizontalOrVerticalAlignment(slope1, slope2))
                return p1.compareTo(p2);
            return slope1.compareTo(slope2);
        };
    }

    private boolean pointsInComparisonAreInHorizontalOrVerticalAlignment(Double slope1, Double slope2) {
        return (slope1 == 0 && slope2 == 0) || (slope1 == Double.POSITIVE_INFINITY && slope2 == Double.POSITIVE_INFINITY);
    }

    private boolean mainPointIsNotAmongComparingPoints(int i, int j) {
        return i < j - 2 || i > j;
    }

    private boolean isSlopeOf4PointsSame(Point[] points, int baseSecondaryComparingIndex) {
        int primaryPointIndex = 0;
        Point primaryPoint = points[primaryPointIndex];
        return points[baseSecondaryComparingIndex - 2].slopeTo(primaryPoint) == points[baseSecondaryComparingIndex - 1].slopeTo(primaryPoint) && points[baseSecondaryComparingIndex - 1].slopeTo(primaryPoint) == points[baseSecondaryComparingIndex].slopeTo(primaryPoint);
    }

    private void addSegment(Point[] points, int index) {
        Point min = min(points[0], points[index - 2]);
        Point max = max(points[0], points[index]);
        LineSegment newLine = new LineSegment(min, max);
        LineSegment inverseLine = new LineSegment(max, min);

        for (LineSegment segment : segments)
            if (segment == null || segment.toString().equals(newLine.toString()) || segment.toString().equals(inverseLine.toString()))
                return;

        if (segmentsIndex >= segments.length - 1)
            doubleSegmentArray();
        segments[segmentsIndex++] = newLine;
    }

    private Point min(Point a, Point b) {
        int LESSER = -1;
        return (a.compareTo(b) == LESSER) ? a : b;
    }

    private Point max(Point a, Point b) {
        int GREATER = 1;
        return (a.compareTo(b) == GREATER) ? a : b;
    }

    private void doubleSegmentArray() {
        LineSegment[] newSegments;
        if (segments.length == 0)
            newSegments = new LineSegment[1];
        else
            newSegments = new LineSegment[segments.length * 2];
        for (int i = 0; i < segments.length; i++)
            newSegments[i] = segments[i];
        segments = newSegments;
    }
}