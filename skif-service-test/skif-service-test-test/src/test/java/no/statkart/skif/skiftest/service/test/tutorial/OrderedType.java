package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.TypeLiteral;

class OrderedType<T> {
    final TypeLiteral<? extends T> type;
    final float order;

    public OrderedType(TypeLiteral<? extends T> type, float order) {
        this.type = type;
        this.order = order;
    }

    public float getOrder() {
        return order;
    }

    public TypeLiteral<? extends T> getType() {
        return type;
    }
}
