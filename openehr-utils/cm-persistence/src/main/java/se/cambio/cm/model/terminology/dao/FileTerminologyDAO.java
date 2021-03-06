package se.cambio.cm.model.terminology.dao;

import se.cambio.cm.model.generic.dao.FileGenericCMElementDAO;
import se.cambio.cm.model.terminology.dto.TerminologyDTO;
import se.cambio.openehr.util.UserConfigurationManager;

public class FileTerminologyDAO extends FileGenericCMElementDAO<TerminologyDTO> {

    public FileTerminologyDAO() {
        super(TerminologyDTO.class, UserConfigurationManager.getTerminologiesFolder());
    }
}
