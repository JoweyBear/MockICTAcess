package Fingerprint;

import java.util.List;

public interface FingerprintDAO {

    FingerprintModel getUserByUserId(String userId);
    List<FingerprintModel> getFingerprints();
}
