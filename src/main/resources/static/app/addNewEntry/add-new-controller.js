(function (){
    var addNewEntryModule = angular.module("addNewEntryModule");
    addNewEntryModule.controller("addNewEntryController",addNewEntryController);

    function addNewEntryController($scope,$http,$location, $routeParams){
        var ctrl = this;
        ctrl.siteName = '';
        ctrl.username='';
        ctrl.password ='';
        ctrl.rePassword ='';
        ctrl.siteUrl='';
        ctrl.message='tester';




        $scope.addNew = addNewEntry;



        function addNewEntry(){

            var userObject = {'username' : ctrl.username,
                              'password' : ctrl.password,
                               'siteName' :ctrl.siteName,
                               'siteUl' : ctrl.siteUrl

            };

            $http.post('/addNewEntry',userObject)
                .then(function (response)
                {
                    ctrl.message = "Your entry is added";
                    resetValues();


                },function(response){
                    ctrl.message = "There was some error creating your account. Sorry about that... Please try again later";
                    resetValues();
                });


        }
        function resetValues(){
            ctrl.siteUrl='';
            ctrl.password='';
            ctrl.username='';
            ctrl.siteName='';
            ctrl.rePassword='';

        }

    }


})();