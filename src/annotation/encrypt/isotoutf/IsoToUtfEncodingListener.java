package annotation.encrypt.isotoutf;

import org.hibernate.event.spi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsoToUtfEncodingListener implements PreInsertEventListener, PreUpdateEventListener, PostLoadEventListener {

    @Autowired
    private IsoToUtfEncodingTransferManager isoToUtfEncodingTransferManager;

    @Override
    public void onPostLoad(PostLoadEvent event) {
//        isoToUtfEncodingTransferManager.reverseTransfer(event.getEntity());
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
        isoToUtfEncodingTransferManager.transfer(state, propertyNames, entity);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
        isoToUtfEncodingTransferManager.transfer(state, propertyNames, entity);
        return false;
    }
}