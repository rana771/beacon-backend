<div class="panel panel-default">
    <div class="panel-heading" style="height: 42px;">BenchmarkStar Transaction Details
        <div class="btn-group pull-right">
            <button id="printPdfBtn" class="k-button k-button-icontext">
                <span class="fa fa-file-pdf-o"></span>&nbsp;Report
            </button>
        </div>
        <app:historyBack
                o_id="${oId}"
                url="${url}">
        </app:historyBack>
    </div>

    <div class="panel-body">
        <div class="panel panel-default">
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td class="active">Name</td>
                    <td><b>${benchmark?.name}</b></td>
                </tr>
                <tr>
                    <td class="active">Total Records</td>
                    <td>${benchmark?.totalRecord*3}&nbsp;(${benchmark?.totalRecord}x3)</td>
                    <td class="active">Record Per Batch</td>
                    <td>${benchmark?.recordPerBatch*3}&nbsp;(${benchmark?.recordPerBatch}x3)</td>
                </tr>
                <tr>
                    <td class="active">Start Time</td>
                    <td>${startTime? startTime : ''}</td>
                    <td class="active">End Time</td>
                    <td>${endTime? endTime :''}</td>
                </tr>
                <tr>
                    <td class="active">Total Time</td>
                    <td>${benchMarkDetails?.total_time}</td>
                    <td class="active">Read Time</td>
                    <td>${benchMarkDetails?.total_read_time} (batch avg  ${benchMarkDetails?.avg_read_time})</td>
                </tr>
                <tr>
                    <td class="active">Processing Time</td>
                    <td>${benchMarkDetails?.total_processing_time} (batch avg  ${benchMarkDetails?.avg_processing_time})</td>
                    <td class="active">Write Time</td>
                    <td>${benchMarkDetails?.total_write_time} (batch avg  ${benchMarkDetails?.avg_write_time})</td>
                </tr>
                <tr>
                    <td class="active">Simulation</td>
                    <td>${benchmark?.isSimulation?'YES':'NO'}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="chart"></div>




