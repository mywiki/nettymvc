<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <title>API文档</title>
    <script>
        <#include "jquery.js"/>
    </script>
    <style>
        body{
            font-size: 12px;
        }
        table.list{
            width: 100%;
            border-collapse: collapse;
            display: none;
        }
        table.list td{width: 25%;border: 1px solid lightgray;padding: 5px;}
        div.title{font-weight: bold;border: 1px solid lightgray;padding: 5px;margin-bottom: 10px;}
        tr.method td{font-weight: bold;color: darkgreen;}
        a{cursor: pointer;}
    </style>
    <script>
        function showApi(obj){
            var id = $(obj).parent("div").attr("id");
            if($("#list_"+id).css("display")=="none"){
                $("#list_"+id).show();
            }else{
                $("#list_"+id).hide();
            }
        }
    </script>
</head>
<body>
${apistrs}
</body>
</html>