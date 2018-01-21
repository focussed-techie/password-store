(function (){
    var dashboardModule = angular.module("dashboardModule");
    dashboardModule.controller("dashboardController",dashboardController);

    function dashboardController($http,alertService,rsaService){
        var ctrl = this;
        ctrl.storage=[];
        ctrl.passwordDisplay = false;

        ctrl.edit=edit;
        ctrl.save=save;
        ctrl.cancel=cancel;
        ctrl.copyToClipboard=copyToClipBoard;
        ctrl.addnewEntry=addnewEntry;
        ctrl.saveNewEntry=saveNewEntry;
        ctrl.decryptData =decryptData;
        ctrl.getPasswordValue =getPasswordValue;



        function addnewEntry(){
           var newStorageObj = {
               'siteName':'',
               siteUrl:'',
               'username':'',
               'password':'',
               'id':'',
               'showPassword':false

           };
            ctrl.storage.push(newStorageObj);
        }

        function saveNewEntry(index){
           var userObject =  ctrl.storage[index];
           userObject.password = rsaService.encryptData(userObject.password);
            $http.post('/addNewEntry',userObject)
                .then(successful).catch(error);
        }

        function successful(){
                alertService.addAlert({type:'warning', message : 'Entry is Successful !!!'});
                getDashboardData();

        }

        function error(){
                alertService.addAlert({type:'danger', message : 'There was an error storing your entry in DB !!!'});
        }


        function display(response){
            ctrl.storage = response.data;
            ctrl.storage.forEach(function (thisValue){
                thisValue.isEditable = false; thisValue.showPassword =false;
          //     thisValue.passwordDisplayValue = rsaService.decryptData(thisValue.password);
            });
            ctrl.prestineStorage = angular.copy(ctrl.storage);


        }

        function decryptData(data){
            return rsaService.decryptData(data);
        }

        function copyToClipBoard(index,event){
            var passwordObj = ctrl.storage[index];
            var password = rsaService.decryptData(passwordObj.password);
            var copyElement = document.getElementById("clipboardData");
            copyElement.value=password;
            copyElement.select();
            document.execCommand("copy");
            copyElement.value="";
            copyElement.blur();
        }

        function edit(index){
            ctrl.storage[index].isEditable = true;
        }

        function save(index){
            var changedValue = ctrl.storage[index];
            changedValue.password = rsaService.encryptData(changedValue.password);
            $http.post("/saveData",changedValue).then(successful).catch(error);

        }
        function cancel(index,id){
           ctrl.storage[index] = angular.copy(ctrl.prestineStorage[index]);
            ctrl.storage[index].isEditable = false;
            ctrl.storage[index].showPassword = false;
        }
        function getDashboardData(){

            $http.get('/dashboard').then(display);
        }


        function getPasswordValue(userdata,index){
            if(ctrl.prestineStorage[index].password === userdata.password){
                return ctrl.decryptData(userdata.password);
            }else {
                return userdata.password;
            }
        }


        rsaService.populateKeys().then(getDashboardData);


    }



})();