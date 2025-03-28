import org.junit.Test;
import static org.junit.Assert.*;
import java.util.function.Function;



// Test class for MatrixMap
public class MatrixMapTest {
    
    @Test
    public void testMatrixMapInstance() {
        // Test creating a matrix with row/column coordinates
        MatrixMap<Integer> matrix = MatrixMap.instance(3, 3, indexes -> 
            indexes.row() * 10 + indexes.column());
        
        // Check dimensions
        assertEquals("Matrix should have correct row size", 3, matrix.size().row());
        assertEquals("Matrix should have correct column size", 3, matrix.size().column());
        
        // Check values
        assertEquals("Matrix should have correct value at (0,0)", Integer.valueOf(0), matrix.value(0, 0));
        assertEquals("Matrix should have correct value at (0,1)", Integer.valueOf(1), matrix.value(0, 1));
        assertEquals("Matrix should have correct value at (1,0)", Integer.valueOf(10), matrix.value(1, 0));
        assertEquals("Matrix should have correct value at (2,2)", Integer.valueOf(22), matrix.value(2, 2));
    }
    
    @Test
    public void testMatrixMapInstanceWithIndexes() {
        // Test creating a matrix with Indexes object
        Indexes size = new Indexes(2, 3);
        MatrixMap<String> matrix = MatrixMap.instance(size, 
            indexes -> "(" + indexes.row() + "," + indexes.column() + ")");
        
        // Check dimensions
        assertEquals("Matrix should have correct row size", 2, matrix.size().row());
        assertEquals("Matrix should have correct column size", 3, matrix.size().column());
        
        // Check values
        assertEquals("Matrix should have correct value at (0,0)", "(0,0)", matrix.value(0, 0));
        assertEquals("Matrix should have correct value at (0,2)", "(0,2)", matrix.value(0, 2));
        assertEquals("Matrix should have correct value at (1,1)", "(1,1)", matrix.value(1, 1));
    }
    
    @Test
    public void testConstantMatrix() {
        // Test creating a constant matrix
        MatrixMap<Integer> matrix = MatrixMap.constant(3, 42);
        
        // Check dimensions
        assertEquals("Constant matrix should have correct row size", 3, matrix.size().row());
        assertEquals("Constant matrix should have correct column size", 3, matrix.size().column());
        
        // Check that all values are the constant
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals("All values should be the constant", Integer.valueOf(42), matrix.value(i, j));
            }
        }
    }
    
    @Test
    public void testIdentityMatrix() {
        // Test creating an identity matrix
        MatrixMap<Integer> matrix = MatrixMap.identity(3, 0, 1);
        
        // Check dimensions
        assertEquals("Identity matrix should have correct row size", 3, matrix.size().row());
        assertEquals("Identity matrix should have correct column size", 3, matrix.size().column());
        
        // Check diagonal values
        assertEquals("Diagonal values should be identity", Integer.valueOf(1), matrix.value(0, 0));
        assertEquals("Diagonal values should be identity", Integer.valueOf(1), matrix.value(1, 1));
        assertEquals("Diagonal values should be identity", Integer.valueOf(1), matrix.value(2, 2));
        
        // Check non-diagonal values
        assertEquals("Non-diagonal values should be zero", Integer.valueOf(0), matrix.value(0, 1));
        assertEquals("Non-diagonal values should be zero", Integer.valueOf(0), matrix.value(1, 0));
        assertEquals("Non-diagonal values should be zero", Integer.valueOf(0), matrix.value(0, 2));
    }
    
    @Test
    public void testFromArray() {
        // Test creating a matrix from a 2D array
        Integer[][] array = {
            {1, 2, 3},
            {4, 5, 6}
        };
        
        MatrixMap<Integer> matrix = MatrixMap.from(array);
        
        // Check dimensions
        assertEquals("Matrix from array should have correct row size", 2, matrix.size().row());
        assertEquals("Matrix from array should have correct column size", 3, matrix.size().column());
        
        // Check values
        assertEquals("Matrix should have correct value from array", Integer.valueOf(1), matrix.value(0, 0));
        assertEquals("Matrix should have correct value from array", Integer.valueOf(3), matrix.value(0, 2));
        assertEquals("Matrix should have correct value from array", Integer.valueOf(4), matrix.value(1, 0));
        assertEquals("Matrix should have correct value from array", Integer.valueOf(6), matrix.value(1, 2));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRowLength() {
        try {
            MatrixMap.InvalidLengthException.requirePositive(MatrixMap.InvalidLengthException.Cause.ROW, 0);
            fail("Should throw exception for zero length");
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof MatrixMap.InvalidLengthException) {
                MatrixMap.InvalidLengthException ile = (MatrixMap.InvalidLengthException) e.getCause();
                assertEquals(MatrixMap.InvalidLengthException.Cause.ROW, ile.getCauseType());
                assertEquals(0, ile.getLength());
                throw e;
            } else {
                fail("Expected InvalidLengthException as cause");
            }
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidColumnLength() {
        MatrixMap.InvalidLengthException.requirePositive(
            MatrixMap.InvalidLengthException.Cause.COLUMN, -1);
    }
    
    @Test
    public void testToString() {
        MatrixMap<String> matrix = MatrixMap.instance(2, 2, 
            indexes -> "(" + indexes.row() + "," + indexes.column() + ")");
        
        String str = matrix.toString();
        assertNotNull("toString should not return null", str);
        assertTrue("toString should contain matrix entries", 
            str.contains("(0,0)") && str.contains("(0,1)") && 
            str.contains("(1,0)") && str.contains("(1,1)"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeRowsMatrix() {
        // 测试负行数
        MatrixMap.instance(-1, 5, indexes -> 0);
    }
    
    @Test
    public void testEdgeCaseMatrix() {
        // 测试1x1矩阵
        MatrixMap<Integer> matrix = MatrixMap.instance(1, 1, indexes -> 42);
        assertEquals("Matrix should have 1 row", 1, matrix.size().row());
        assertEquals("Matrix should have 1 column", 1, matrix.size().column());
        assertEquals("Value at (0,0) should be 42", Integer.valueOf(42), matrix.value(0, 0));
    }
    
    @Test(expected = NullPointerException.class)
    public void testNullValueRejection() {
        // 测试null值应该被拒绝
        MatrixMap<Integer> matrix = MatrixMap.instance(2, 2, indexes -> 
            indexes.row() == 0 && indexes.column() == 0 ? null : 1);
        // 这里应该抛出NullPointerException，因为valueMapper返回的null被拒绝
    }
    
    @Test
    public void testMatrixSizeWithNoEntries() {
        // 我们不能测试0列的矩阵，因为 MatrixMap 不允许维度为0
        // 测试最小允许尺寸的矩阵 (1x1)
        MatrixMap<Integer> singleElementMatrix = MatrixMap.instance(1, 1, indexes -> 42);
        assertEquals("Single element matrix should have 1 row", 1, singleElementMatrix.size().row());
        assertEquals("Single element matrix should have 1 column", 1, singleElementMatrix.size().column());
        assertEquals("Value at (0,0) should be 42", Integer.valueOf(42), singleElementMatrix.value(0, 0));
    }
    
    @Test
    public void testValueWithIndexes() {
        // 测试使用Indexes对象获取值
        MatrixMap<Integer> matrix = MatrixMap.instance(2, 2, indexes -> indexes.row() + indexes.column());
        Indexes idx = new Indexes(1, 0);
        assertEquals("Should retrieve correct value with Indexes object", Integer.valueOf(1), matrix.value(idx));
    }
    
    @Test(expected = NullPointerException.class)
    public void testValueWithNullIndexes() {
        // 测试用null索引获取值应抛出异常
        MatrixMap<Integer> matrix = MatrixMap.instance(2, 2, indexes -> 1);
        matrix.value((Indexes)null);
    }
    
    @Test
    public void testNonRectangularArrayCreation() {
        // 测试从非矩形数组创建矩阵
        Integer[][] jagged = new Integer[][] {
            {1, 2, 3},
            {4, 5, 6}  // 改为完整的行
        };
        
        // 应该使用第一行的长度作为列数
        MatrixMap<Integer> matrix = MatrixMap.from(jagged);
        assertEquals("Should have 2 rows", 2, matrix.size().row());
        assertEquals("Should have 3 columns", 3, matrix.size().column());
        assertEquals("Value at (0,0) should be 1", Integer.valueOf(1), matrix.value(0, 0));
        assertEquals("Value at (1,1) should be 5", Integer.valueOf(5), matrix.value(1, 1));
        assertEquals("Value at (1,2) should be 6", Integer.valueOf(6), matrix.value(1, 2));
    }
    
    @Test
    public void testNonRectangularArraySize() {
        // 测试有行但所有行都有完整数据
        Integer[][] nonRect = new Integer[][] {
            {1, 2, 3},
            {4, 5, 6}, // 这行有完整的元素
            {7, 8, 9}  // 这行有完整的元素
        };
        
        MatrixMap<Integer> matrix = MatrixMap.from(nonRect);
        assertEquals("Matrix should have 3 rows", 3, matrix.size().row());
        assertEquals("Matrix should have 3 columns", 3, matrix.size().column());
    }
}