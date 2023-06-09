package springsaga.external;

import java.util.Date;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springsaga.domain.*;

@FeignClient(name = "Storage", url = "${api.url.Storage}")
public interface StorageService {
    @RequestMapping(method = RequestMethod.POST, path = "/storages")
    public void increaseStock(@RequestBody Storage storage);
}
