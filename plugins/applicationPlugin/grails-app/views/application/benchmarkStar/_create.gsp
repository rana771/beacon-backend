<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridBenchmarkStar'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Benchmark Star
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="benchmarkStarForm" name="benchmarkStarForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: benchmarkStar.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: benchmarkStar.version"/>

                    <app:systemEntityByReserved name="hidEntityTypeId"
                                                reservedId="${AppSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR}"
                                                typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG}"
                                                pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           data-bind="value: benchmarkStar.name" placeholder="Benchmark Star Name"
                                           required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="isSimulation">Simulation:</label>

                                <div class="col-md-3">
                                    <input type="checkbox" id="isSimulation" tabindex="2" name="isSimulation"
                                                data-bind="checked: benchmarkStar.isSimulation"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-4 control-label label-required"
                                       for="totalRecord">Total Records:</label>

                                <div class="col-md-5">
                                    <input type="text" id="totalRecord" name="totalRecord" tabindex="3"
                                           data-bind="value: benchmarkStar.totalRecord"
                                           placeholder="Positive value" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="totalRecord"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label label-required"
                                       for="recordPerBatch">Record Per Batch:</label>

                                <div class="col-md-5">
                                    <input type="text" id="recordPerBatch" name="recordPerBatch" tabindex="4"
                                           data-bind="value: benchmarkStar.recordPerBatch"
                                           placeholder="min:50,max:5000" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="recordPerBatch"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/benchmarkStar/create,/benchmarkStar/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="5"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

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
        <div id="gridBenchmarkStar"></div>
    </div>
</div>




