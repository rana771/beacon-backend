<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="registration"/>
    <script>
        var dataSource;
        $(document).attr('title', 'All Notes');
    </script>
</head>
<body>
<div class="container-fluid no-padding-margin" id="noteContainer" style=" max-height: 700px; ">
    <app:appNote
            id="notes" datasource="dataSource" entity_type_id="${params.entityTypeId}" order="desc" result_per_page="10"
            entity_id="${params.entityId}" title="${params.title? params.title : "All Notes"}"/>
</div>
</body>
</html>