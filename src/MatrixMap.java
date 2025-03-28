import java.util.*;
import java.util.function.Function;

// May contain bug(s)
public final class MatrixMap<T> {

    // 内部异常类，修改了方法名称以符合 Java 命名习惯
    public static class InvalidLengthException extends Exception {
        public enum Cause {
            ROW,
            COLUMN
        }

        private final Cause cause;
        private final int length;

        public InvalidLengthException(Cause cause, int length) {
            this.cause = cause;
            this.length = length;
        }

        public Cause getCauseType() {
            return cause;
        }

        public int getLength() {
            return length;
        }

        // 修改方法名称为 requirePositive，更符合其行为（要求正数）
        public static int requirePositive(Cause cause, int length) {
            if(length <= 0) {
                throw new IllegalArgumentException(new InvalidLengthException(cause, length));
            }
            return length;
        }
    }

    private final RoamingMap<Indexes, T> matrix;

    private MatrixMap(RoamingMap<Indexes, T> matrix) {
        this.matrix = matrix;
    }

    public static <S> MatrixMap<S> instance(int rows, int columns, Function<Indexes, S> valueMapper) {
        Objects.requireNonNull(valueMapper);
        RoamingMap<Indexes, S> matrix = buildMatrix(rows, columns, valueMapper);
        return new MatrixMap<>(matrix);
    }

    public static <S> MatrixMap<S> instance(Indexes size, Function<Indexes, S> valueMapper) {
        Objects.requireNonNull(size);
        Objects.requireNonNull(valueMapper);
        RoamingMap<Indexes, S> matrix = buildMatrix(size.row(), size.column(), valueMapper);
        return new MatrixMap<>(matrix);
    }

    public static <S> MatrixMap<S> constant(int size, S value) {
        Objects.requireNonNull(value);
        return instance(size, size, indexes -> value);
    }

    public static <S> MatrixMap<S> identity(int size, S zero, S identity) {
        Objects.requireNonNull(zero);
        Objects.requireNonNull(identity);
        // 修正了 lambda 表达式：对角线位置应为 identity，其他位置为 zero
        return instance(size, size, indexes -> (indexes.areDiagonal() ? identity : zero));
    }

    public static <S> MatrixMap<S> from(S[][] matrix) {
        Objects.requireNonNull(matrix);
        int columns = InvalidLengthException.requirePositive(InvalidLengthException.Cause.COLUMN, matrix[0].length);
        int rows = InvalidLengthException.requirePositive(InvalidLengthException.Cause.ROW, matrix.length);
        return instance(rows, columns, indexes -> indexes.value(matrix));
    }

    public Indexes size() {
        int maxRow = -1;
        int maxCol = -1;
        for (Indexes idx : Barricade.correctKeySet(matrix)) {
            if (idx.row() > maxRow) {
                maxRow = idx.row();
            }
            if (idx.column() > maxCol) {
                maxCol = idx.column();
            }
        }
        return new Indexes(maxRow + 1, maxCol + 1);
    }

    @Override
    public String toString() {
        return Barricade.correctStringRepresentation(matrix);
    }

    public T value(Indexes indexes) {
        Objects.requireNonNull(indexes);
        return Barricade.safeGet(matrix, indexes).value();
    }

    public T value(int row, int column) {
        return value(new Indexes(row, column));
    }

    private static <S> RoamingMap<Indexes, S> buildMatrix(int rows, int columns, Function<Indexes, S> valueMapper) {
        int rowsNumber = InvalidLengthException.requirePositive(InvalidLengthException.Cause.ROW, rows);
        int columnsNumber = InvalidLengthException.requirePositive(InvalidLengthException.Cause.COLUMN, columns);
        RoamingMap<Indexes, S> matrix = new RoamingMap<>();
        // 使用 for 循环而不是 stream 来确保创建正确大小的矩阵
        for (int i = 0; i < rowsNumber; i++) {
            for (int j = 0; j < columnsNumber; j++) {
                Indexes idx = new Indexes(i, j);
                S value = valueMapper.apply(idx);
                Barricade.safePut(matrix, idx, value);
            }
        }
        return matrix;
    }
}
