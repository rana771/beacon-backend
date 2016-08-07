<div class="container-fluid">
    <div class="row">

        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppPage'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Page
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="appPageForm" name='appPageForm' class="form-horizontal form-widgets" role="form" method="post">
                <div class="panel-body" style="display: none;">
                    <input type="hidden" id="id" name="id" data-bind="value: appPage.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: appPage.version"/>
                    <input type="hidden" id="entityTypeId" name="entityTypeId" data-bind="value: appPage.entityTypeId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="title">Title:</label>
                    </div>

                    <div class="form-group">
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" id="title" name="title"
                                   tabindex="1"
                                   maxlength="255" data-bind="value: appPage.title"
                                   placeholder="Title"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="title"></span>
                        </div>

                            <label class="col-md-2 control-label"
                                   for="isCommentable">Is Comment Allow:</label>

                            <div class="col-md-3">
                                <input type="checkbox" id="isCommentable" tabindex="2"
                                       name="isCommentable"
                                       data-bind="checked: appPage.isCommentable"/>
                            </div>
                    </div>

                    <div class="form-group">

                        <label class="col-md-1 control-label label-required" for="body">Body:</label>

                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <textarea type="text" class="k-textbox" id="body" name="body" rows="5"
                                      maxlength="2040"
                                      placeholder="Dear '$'{userName}, Here is the Body part....."
                                      required=""
                                      tabindex="3"
                                      data-bind="value: appPage.body"
                                      validationMessage="Required"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <span class="k-invalid-msg" data-for="body"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none;">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppPage"></div>
    </div>
</div>