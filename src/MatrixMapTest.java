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
    
    @Test(expected = MatrixMap.InvalidLengthException.class)
    public void testInvalidRowLength() throws MatrixMap.InvalidLengthException {
        // This should throw an InvalidLengthException
        MatrixMap.InvalidLengthException.requireNonEmpty(
            MatrixMap.InvalidLengthException.Cause.ROW, 0);
    }
    
    @Test(expected = MatrixMap.InvalidLengthException.class)
    public void testInvalidColumnLength() throws MatrixMap.InvalidLengthException {
        // This should throw an InvalidLengthException
        MatrixMap.InvalidLengthException.requireNonEmpty(
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
}