package no.statkart.skif.service.proxy;

import no.statkart.skif.service.annotation.SuppressSnapshotVersionMapping;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatCode;

public class SnapshotVersionArgumentListAnalyserTest {
    
    interface FooService {
        void bar(
            @org.jspecify.annotations.Nullable BubbleId<?> nullableJSpecify,
            @jakarta.annotation.Nullable BubbleId<?> nullableJakarta,
            @javax.annotation.Nullable BubbleId<?> nullableJavax,
            @SuppressSnapshotVersionMapping BubbleId<?> supressedId,
            BubbleId<?> nonNull
        );
    }
    
    static class FooId extends AbstractBubbleId<AbstractBubbleObject> {
    }
    
    private static Method barMethod() {
        try {
            return FooService.class.getDeclaredMethod("bar", BubbleId.class, BubbleId.class, BubbleId.class, BubbleId.class, BubbleId.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void analyseD2W_null_parameters() {
        var snapshotVersionArgumentListAnalyser = new SnapshotVersionArgumentListAnalyser();
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{new FooId(), new FooId(), new FooId(), new FooId(), new FooId()}))
            .doesNotThrowAnyException();
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{new FooId(), new FooId(), new FooId(), null, new FooId()}))
            .as("ignore null når annotert med @SuppressSnapshotVersionMapping")
            .doesNotThrowAnyException();
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{new FooId(), new FooId(), null, new FooId(), new FooId()}))
            .as("ignore null når annotert med @javax.annotation.Nullable")
            .doesNotThrowAnyException();
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{new FooId(), null, new FooId(), new FooId(), new FooId()}))
            .as("ignore null når annotert med @jakarta.annotation.Nullable")
            .doesNotThrowAnyException();
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{null, new FooId(), new FooId(), new FooId(), new FooId()}))
            .as("ignore null når annotert med @org.jspecify.annotations.Nullable")
            .doesNotThrowAnyException();
        
        //positiv test
        assertThatCode(() -> snapshotVersionArgumentListAnalyser.analyseD2W(barMethod(), new Object[]{null, new FooId(), new FooId(), new FooId(), null}))
            .hasMessage("Parameter with index '4' of subtype BubbleId is null, did you forget to annotate with @Nullable?");
    }
}
