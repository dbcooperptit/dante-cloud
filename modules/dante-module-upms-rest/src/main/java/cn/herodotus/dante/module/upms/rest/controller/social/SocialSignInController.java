/*
 * Copyright (c) 2020-2030 ZHENGGENGWEI(码匠君)<herodotus@aliyun.com>
 *
 * Dante Cloud licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Dante Cloud 采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改 Dante Cloud 源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://gitee.com/dromara/dante-cloud
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/dromara/dante-cloud
 * 6.若您的项目无法满足以上几点，可申请商业授权
 */

package cn.herodotus.dante.module.upms.rest.controller.social;

import cn.herodotus.engine.assistant.core.domain.AccessPrincipal;
import cn.herodotus.engine.assistant.core.domain.Result;
import cn.herodotus.engine.oauth2.authorization.definition.AbstractSocialAuthenticationHandler;
import cn.herodotus.engine.oauth2.core.definition.domain.HerodotusUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>Description: 社交用户登录接口 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/6/20 18:19
 */
@RestController
@RequestMapping("/social")
@Tag(name = "社交用户登录接口")
public class SocialSignInController {

    private final AbstractSocialAuthenticationHandler socialAuthenticationHandler;

    @Autowired
    public SocialSignInController(AbstractSocialAuthenticationHandler socialAuthenticationHandler) {
        this.socialAuthenticationHandler = socialAuthenticationHandler;
    }

    @Operation(summary = "社交登录用户信息查询", description = "根据不同的source查询对应社交用户的信息")
    @Parameters({
            @Parameter(name = "socialDetails", required = true, description = "系统用户名", in = ParameterIn.PATH),
    })
    @PostMapping("/sign-in/{source}")
    public Result<HerodotusUser> findUserDetailsBySocial(@PathVariable("source") String source, @RequestBody AccessPrincipal accessPrincipal) {
        HerodotusUser herodotusUser = this.socialAuthenticationHandler.authentication(source, accessPrincipal);
        if (ObjectUtils.isNotEmpty(herodotusUser)) {
            return Result.success("社交登录成功", herodotusUser);
        } else {
            return Result.failure("社交登录失败，未能查到用户数据");
        }
    }
}