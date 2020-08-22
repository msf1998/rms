app.service("fileService",function ($http) {
    this.listFiles = function () {
        return $http.get("/manage/api/file/list");
    }

    this.createDirectory = function (obj) {
        return $http.post("/manage/api/file/create/directory",obj);
    }

    this.createDocument = function (form) {
        return $http({
            url: "/manage/api/file/create/document",
            method: "post",
            transformRequest: angular.identity,
            headers:{
                'Content-Type': undefined
            },
            data: form
        })
    }

    this.deleteFile = function (obj) {
        return $http.post("/manage/api/file/delete",obj);
    }

    this.rename = function (obj) {
        return $http.post("/manage/api/file/rename",obj);
    }
})