package cn.herodotus.eurynome.upms.rest.controller.hr;

import cn.herodotus.eurynome.component.data.base.service.BaseService;
import cn.herodotus.eurynome.component.rest.controller.BaseRestController;
import cn.herodotus.eurynome.upms.api.entity.hr.SysPosition;
import cn.herodotus.eurynome.upms.logic.service.hr.SysPositionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/position")
@Api(tags = {"用户中心服务", "岗位管理接口"})
public class SysPositionController extends BaseRestController<SysPosition, String> {

    private final SysPositionService sysPositionService;

    @Autowired
    public SysPositionController(SysPositionService sysPositionService) {
        this.sysPositionService = sysPositionService;
    }

    @Override
    public BaseService<SysPosition, String> getBaseService() {
        return this.sysPositionService;
    }
}
