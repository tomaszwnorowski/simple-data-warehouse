package sdw.test;

import java.lang.annotation.*;
import org.junit.jupiter.api.Tag;
import org.springframework.test.context.ActiveProfiles;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("unit")
@Tag("unit")
public @interface UnitTest {}
