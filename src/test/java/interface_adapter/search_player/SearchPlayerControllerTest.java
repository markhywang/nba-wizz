package interface_adapter.search_player;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPlayerControllerTest {

    @Test
    void executeSearchCallsInteractor() throws Exception {
        Class<?> boundaryClass = Class.forName("use_case.search_player.SearchPlayerInputBoundary");

        AtomicReference<Method> invokedMethod = new AtomicReference<>();
        AtomicReference<Object[]> invokedArgs = new AtomicReference<>();

        Object interactorProxy = Proxy.newProxyInstance(
                boundaryClass.getClassLoader(),
                new Class<?>[]{boundaryClass},
                (proxy, method, args) -> {
                    invokedMethod.set(method);
                    invokedArgs.set(args);
                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(void.class)) {
                        return null;
                    } else if (returnType.equals(boolean.class)) {
                        return false;
                    } else if (returnType.equals(byte.class)) {
                        return (byte) 0;
                    } else if (returnType.equals(short.class)) {
                        return (short) 0;
                    } else if (returnType.equals(int.class)) {
                        return 0;
                    } else if (returnType.equals(long.class)) {
                        return 0L;
                    } else if (returnType.equals(float.class)) {
                        return 0f;
                    } else if (returnType.equals(double.class)) {
                        return 0d;
                    } else if (returnType.equals(char.class)) {
                        return '\0';
                    }
                    return null;
                }
        );

        Constructor<?> controllerConstructor = SearchPlayerController.class.getConstructor(boundaryClass);
        Object controller = controllerConstructor.newInstance(interactorProxy);

        List<String> stats = Arrays.asList("PTS", "AST");
        ((SearchPlayerController) controller).executeSearch("LeBron James", "2000", "2010", stats);

        assertNotNull(invokedMethod.get());
        assertEquals("execute", invokedMethod.get().getName());
        assertNotNull(invokedArgs.get());
        assertEquals(1, invokedArgs.get().length);
        assertNotNull(invokedArgs.get()[0]);
    }
}
