# API reference

host: https://fcimappapi.azurewebsites.net/

### Authentication endpoints
Documentation TBD. Currently only limited implementation is active, with no refresh tokens

### Data access endpoints
- api/teachers <span style="color:green">[*Implemented*]</span></br>
Get list of teachers that are registered in the system. </br>
<span style="color:gray">*Returns:*</span> list of dbdataaccess/src/main/java/pojos/Teacher.java objects

- api/groups <span style="color:green">[*Implemented*]</span></br>
Get list of groups that are registered in the system.</br> 
<span style="color:gray">*Returns:*</span> list of dbdataaccess/src/main/java/pojos/Group.java objects


- api/classrooms <span style="color:green">[*Implemented*]</span><br/>
Get list of classrooms that are registered in the system.<br/> 
<span style="color:gray">*Returns:*</span> list of dbdataaccess/src/main/java/pojos/Classroom.java objects


- api/schedule/groups/{group_id}/{date} <span style="color:green">[*Implemented*]</span><br/>
Get schedule for given GROUP and date. <br/>
<span style="color:gray">*Returns:*</span> list of dbdataaccess/src/main/java/pojos/ScheduleEntry.java objects <br/>
<span style="color:gray">*Params:*</span> <br/>
&nbsp;&nbsp;1\) Group, as "internal" group id value, not number (i.e. NOT 'TI-154');<br/>
&nbsp;&nbsp;2\) Date, in "yyyy-MM-dd" format <br/>

- api/schedule/teachers/{teacher_id}/{date} <span style="color:green">[*Implemented*]</span><br/>
Get schedule for given TEACHER and date. <br/>
<span style="color:gray">*Returns:*</span> list of dbdataaccess/src/main/java/pojos/ScheduleEntry.java objects <br/>
<span style="color:gray">*Params:*</span> <br/>
&nbsp;&nbsp;1\) Teacher, by ID<br/>
&nbsp;&nbsp;2\) Date, in "yyyy-MM-dd" format <br/>

- api/schedule/groups/{group_id} <span style="color:red">[*TBD*]</span><br/>
Get entire schedule for given group for current semester <br/>

- api/schedule/teachers/{teacher_id} <span style="color:red">[*TBD*]</span><br/>
Get entire schedule for given teacher for current semester <br/>
