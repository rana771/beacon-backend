<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.config.AppSysConfigCacheService" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <span class="pull-left"><i class="fa fa-user"></i></span>

        <div class="panel-title">
            Update Profile
        </div>
    </div>

    <g:form name='userForm' id='userForm' class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>

        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-8">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="username">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="username" name="username" maxlength="255"
                                   value="${appUser ? appUser.username : ""}"
                                   placeholder="User Name" required validationMessage="Required" tabindex="1"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="username"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label id="lblEmail" class="col-md-3 control-label"
                               for="email">Email:</label>

                        <div class="col-md-6">
                            <input type="email" class="k-textbox" id="email" name="email" maxlength="255"
                                   placeholder="Email" tabindex="2"
                                   value="${appUser ? appUser.email : ""}"/>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="cellNumber">Cell Number:</label>

                        <div class="col-md-6">
                            <input type="tel" class="k-textbox" id="cellNumber" name="cellNumber"
                                   pattern="<app:showSysConfig
                                           key="${AppSysConfigCacheService.APPLICATION_PHONE_PATTERN}"
                                           pluginId="${PluginConnector.PLUGIN_ID}">
                                   </app:showSysConfig>" tabindex="3"
                                   placeholder="Mobile Number" validationMessage="Invalid phone No."
                                   value="${appUser ? appUser.cellNumber : ""}"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label" for="dateOfBirth">Date of Birth :</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="dateOfBirth"
                                    tabindex="4"
                                    value="${appUserDetails?.dateOfBirth ? appUserDetails.dateOfBirth : ""}"
                                    placeholder="${message(code: 'elportal.common.date.placeholder', default: 'dd/MM/yyyy')}">
                            </app:dateControl>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label" >Gender :</label>

                        <div class="col-md-3">
                            <g:if test="${genderMaleId == appUser.genderId}">
                                <input type="radio" name="genderId" id="genderId" value="${genderMaleId}"
                                       checked="checked"/>&nbsp;Male
                            </g:if>
                            <g:else>
                                <input type="radio" name="genderId" id="genderId" value="${genderMaleId}"/>&nbsp;Male
                            </g:else>
                        </div>

                        <div class="col-md-3">
                            <g:if test="${genderFeMaleId == appUser.genderId}">
                                <input type="radio" name="genderId" id="genderId" value="${genderFeMaleId}" checked="checked"/>&nbsp;Female
                            </g:if>
                            <g:else>
                                <input type="radio" name="genderId" id="genderId" value="${genderFeMaleId}"/>&nbsp;Female
                            </g:else>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="address">Address :</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="address" name="address" rows="3"
                                      placeholder="255 Char Max" tabindex="7"
                                      maxlength="255">${appUserDetails ? appUserDetails.address : ""}</textarea>
                        </div>
                    </div>


                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="signatureImage">Picture:</label>

                        <div class="col-md-6">
                            <input type="file" class="form-control-static" id="signatureImage" tabindex="8"
                                   name="signatureImage"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="userDocument">Resume:</label>

                        <div class="col-md-6">
                            <input type="file" class="form-control-static" id="userDocument" tabindex="9"
                                   name="userDocument"/>
                        </div>
                    </div>

                </div>

                <div class="col-md-4">
                    <div class="form-group">
                        <div class="col-md-2"></div>
                        <div class="col-md-6">
                            <img class="img-thumbnail img-fluid"
                                 src='${createLink(controller: 'appUser', action: 'renderProfileImage')}?a=${new Date()}'>
                        </div>
                    </div>

                    <g:if test="${appAttachmentId > 0}">
                        <div class="form-group">
                            <br/>
                            <div class="col-md-2"></div>
                            <div class="col-md-6">
                                <button type="button" class="btn btn-primary btn-block"
                                        onclick="downloadUserDocument('${appAttachmentId}')">Download Resume
                                </button>
                            </div>
                        </div>
                    </g:if>

                    <div class="form-group">
                        <br/>
                        <label class="col-md-1 control-label label-optional" for="facebookUrl"><i
                                class="fa fa-facebook-official"></i></label>

                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="facebookUrl" name="facebookUrl" maxlength="255"
                                   value="${appUserDetails ? appUserDetails.facebookUrl : ""}"
                                   placeholder="Facebook url" tabindex="10"/>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="webUrl"><i class="fa fa-link"></i>
                        </label>

                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="webUrl" name="webUrl" maxlength="255"
                                   value="${appUserDetails ? appUserDetails.webUrl : ""}"
                                   placeholder="Web url" tabindex="11"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="nationalId">NID</label>

                        <div class="col-md-8">
                            <input type="text" class="k-textbox" id="nationalId" name="nationalId" maxlength="255"
                                   value="${appUserDetails ? appUserDetails.nationalId : ""}"
                                   placeholder="National ID Number" tabindex="12"/>
                        </div>
                    </div>

                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional"
                       for="aboutMe">About Me:</label>

                <div class="col-md-9">
                    <textarea style="width: 100%" class="k-textbox" name="aboutMe" id="aboutMe" maxlength="5000"
                              tabindex="11">${appUserDetails ? appUserDetails.aboutMe : ""}</textarea>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="12"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Save Changes
            </button>
        </div>
    </g:form>
</div>