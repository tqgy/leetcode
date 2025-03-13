package com.carrerup.array;

import org.junit.Test;
import static org.junit.Assert.*;

public class AllUniqueCharactersTest {
    @Test
    public void testIsAllUniqueChars() {
        assertFalse(AllUniqueCharacters.isAllUniqueChars(null));
        assertFalse(AllUniqueCharacters.isAllUniqueChars(new char[]{}));
        assertTrue(AllUniqueCharacters.isAllUniqueChars(new char[]{'a', 'b', 'c'}));
        assertFalse(AllUniqueCharacters.isAllUniqueChars(new char[]{'a', 'b', 'a'}));
        assertTrue(AllUniqueCharacters.isAllUniqueChars(new char[]{'1', '2', '3', '4'}));
        assertFalse(AllUniqueCharacters.isAllUniqueChars(new char[]{'1', '2', '3', '1'}));
    }
}
