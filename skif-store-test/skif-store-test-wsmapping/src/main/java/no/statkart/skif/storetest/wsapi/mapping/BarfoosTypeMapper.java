package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.BarFoos;
import no.statkart.skif.storetest.domain.Foo;
import no.statkart.skif.storetest.domain.FooId;
import no.statkart.skif.storetest.wsapi.domain.FooIdList;

import java.util.HashSet;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class BarfoosTypeMapper extends StoreTestBubbleTypeMapper<no.statkart.skif.storetest.wsapi.domain.BarFoos, BarFoos> {
    public BarfoosTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.BarFoos.class, BarFoos.class);
    }

    @Override
    public void mapDomainObject(BarFoos source, no.statkart.skif.storetest.wsapi.domain.BarFoos target) {
        super.mapDomainObject(source, target);   
        
        target.setText(map.d2w(source.getText()));
        target.setBarId(map.d2w(source.getBarId()));
        target.setFooIds(new FooIdList());
        map.d2w(source.getFooIds(), target.getFooIds());
    }

    @Override
    public void mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.BarFoos source, BarFoos target) {
        super.mapWsapiObject(source, target);    

        target.setText(map.w2d(source.getText()));
        target.setBarId(map.w2d(source.getBarId()));
        target.setFooIds(new HashSet<FooId<Foo>>());
        map.w2d(source.getFooIds(), target.getFooIds());
    }
}
