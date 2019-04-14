package coursera.algorithms.sort;

import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private Point[] points;
    private LineSegment[] segments;
    private int segmentsIndex;

    public FastCollinearPoints(Point[] points) {
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
        for (Point point : points)
            if (point == null)
                return true;
        return false;
    }

    private boolean arePointsRepeated(Point[] points) {
        int EQUAL = 0;

        for (int i = 0; i < points.length; i++)   // brute or sorted check? what will be the comparator
            for (int j = i + 1; j < points.length; j++)
                if (points[i].compareTo(points[j]) == EQUAL)
                    return true;
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
        int baseIndex = 0;
        Point[] pointsCopy = new Point[points.length];

        for (int i = 0; i < points.length; i++)
            pointsCopy[i] = points[i];

        for (int i = 0; i < points.length; i++) {
            Arrays.sort(pointsCopy, customSlopeOrder(points[i]));
            for (int j = 1; j < points.length; j++) {
                int initialIndex = j;
                double initialSlope = pointsCopy[baseIndex].slopeTo(pointsCopy[j++]);
                while (j < points.length && pointsCopy[baseIndex].slopeTo(pointsCopy[j]) == initialSlope)
                    j++;

                if (thereAreEnoughCollinearPoints(initialIndex, j - 1))
                    addSegment(pointsCopy, initialIndex, j - 1);
            }
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

    private boolean thereAreEnoughCollinearPoints(int startIndex, int endIndex) {
        int MIN_COLLINEAR_POINTS = 4;
        int basePoint = 1;
        return basePoint + endIndex - startIndex + 1 >= MIN_COLLINEAR_POINTS;
    }

    private void addSegment(Point[] points, int startIndex, int endIndex) {
        int baseIndex = 0;
        Point min = min(points[baseIndex], points[startIndex]);
        Point max = max(points[baseIndex], points[endIndex]);
        LineSegment newLine = new LineSegment(min, max);
        LineSegment reverseOfNewLine = new LineSegment(max, min);

        if (isSegmentAlreadyPresentInArray(segments, newLine, reverseOfNewLine))
            return;

        doesSegmentArrayHaveEnoughSize();
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

    private boolean isSegmentAlreadyPresentInArray(LineSegment[] segments, LineSegment line, LineSegment reverseOfLine) {
        for (LineSegment segment : segments)
            if (segment == null || isLineEqual(segment, line, reverseOfLine))
                return true;
        return false;
    }

    private boolean isLineEqual(LineSegment primary, LineSegment secondary, LineSegment reverseOfSecondary) {
        return primary.toString().equals(secondary.toString()) || primary.toString().equals(reverseOfSecondary.toString());
    }

    private void doesSegmentArrayHaveEnoughSize() {
        if (segmentsIndex >= segments.length - 1)
            doubleSegmentArray();
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