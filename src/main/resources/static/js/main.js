//Component must be declared first
Vue.component('img-table-row', {
	data: function (a) {
		//debugger;
	    return {
	      item:this.imgData
	    }
	},
	props: ['imgData'],
	methods: {
		doDelete: item => {
			//alert(item.path);
            /*var formData = new FormData();
            var widget = $("#uploadFile").get(0).files;
            for(let i=0;i<widget.length;i++){
          	  formData.append('files', widget[i]);
            }
            formData.append("username", app.username);*/
            
            $.ajax({
                url: "delete",
                type: "post",
                dataType: "json",
                cache: false,
                data: {
                	path:item.path,
                	username: app.username,
                	owner:item.username
                },
                success: data => {
                    alert(data.msg);
                    app.doLoad();
                },
                error: resp => {
              	  alert(resp.responseJSON.msg);
                }
            })
            
          }
    },
	template: '<tr><td>{{item.key}}</td><td>{{item.name}}</td><td>{{item.path}}</td><td>{{item.username}}</td>'+
		'<td><button class="btn btn-danger btn-sm" v-on:click="doDelete(item)">Delete</button></td></tr>'
})
Vue.component('custom-button', {
  template: '<button class="btn btn-danger btn-sm" >Delete</button>'
});

var app = new Vue({
	el: '#app',
	//Model
	data: {
		users: [],
		items: [],
		files: [],
		username: "public",
	},
	mounted() {
		//Get config user list
		$.ajax({ url: "/userlist.json", dataType: "json",
            success: function(data){
                app.users = data.list;
            },
            error: function(data){
                console.log("Failed to load users!");
            }
        });
		
		this.doLoad();
		/*fetch('/mock')
		  .then(resp => {
			  console.log(resp);
		      return resp.json();
		  })
		  .then(json => {
			  console.log(json);
		  })*/
    },
    
    methods: {
    	doLoad: data =>{
    		$.ajax({ url: "/files", dataType: "json",
                success: function(data){
                    app.items = data;
                },
                error: function(data){
                    console.log("Failed to load file list!");
                }
            });
    	},
        doUpload: event => {
          var formData = new FormData();
          var widget = $("#uploadFile").get(0).files;
          for(let i=0;i<widget.length;i++){
        	  formData.append('files', widget[i]);
          }
          formData.append("username", app.username);
          //formData.append("model", {key:1,name:"test"});
          
          $.ajax({
              url: "upload",
              type: "post",
              dataType: "json",
              cache: false,
              data: formData,
              processData: false,
              contentType: false,
              //ES6
              success: data => {
                  console.log(data);
              },
              /*success: function(resp){
            	  console.log(data);
              },*/
              error: msg => {
            	  console.error(msg.responseJSON);
            	  alert(msg.responseJSON);
              }
          })
          
        }
    }
})
