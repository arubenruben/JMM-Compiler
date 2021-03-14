import org.junit.Test;
import pt.up.fe.comp.TestUtils;

import static org.junit.Assert.assertEquals;

public class ExampleTest {



    @Test
    public void testExpression() {		
		assertEquals("Expression", TestUtils.parse("2+3\n").getRootNode().getKind());		
	}


}
