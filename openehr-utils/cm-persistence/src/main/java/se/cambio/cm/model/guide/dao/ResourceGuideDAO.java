package se.cambio.cm.model.guide.dao;

import se.cambio.cm.model.generic.dao.ResourceGenericCMElementDAO;
import se.cambio.cm.model.guide.dto.GuideDTO;

public class ResourceGuideDAO extends ResourceGenericCMElementDAO<GuideDTO> {

    public ResourceGuideDAO() {
        super(GuideDTO.class);
    }
}
