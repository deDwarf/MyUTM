# API reference

host: https://fcimappapi.azurewebsites.net/

### Authentication endpoints
- **POST** api/auth/token <br/>
Receive access token. This token should be securely saved and then used for further requests authorization. <br/>
*Returns:* json object with the only element - "access_token"<br/>
*Query Params:* <br/>
&nbsp;&nbsp;1\) *grant_type*, accepts two values: "password" OR "refresh_token". If "password" is given, look for "username" and "password" request params. Otherwise, look for "refresh_token"<br/>
&nbsp;&nbsp;2\) *username*, user login (for grant_type="password") <br/>
&nbsp;&nbsp;3\) *password*, user password (for grant_type="password") <br/>
&nbsp;&nbsp;4\) *refresh_token*, for grant_type="refresh)token"<br/>

- **POST** api/auth/register/student <br/>
Receive access token to be used for further requests authorization. <br/>
*Returns:* json containing [success] field with "true" or "false" value. If false, also includes additional [reason] and [reasonCode] elems. <br/>
*Query Params:* <br/>
&nbsp;&nbsp;1\) *group_id*, optional param. Student's group internal ID<br/>
&nbsp;&nbsp;2\) *username*, mandatory <br/>
&nbsp;&nbsp;3\) *password*, mandatory <br/>
&nbsp;&nbsp;4\) *firstNm*, mandatory<br/>
&nbsp;&nbsp;4\) *secondNm*, mandatory<br/>



### User-dependent Endpoints

- **GET** api/profile <br/>
Get personal profile information <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/Student.java OR ../Teacher.java objects (depends on requestor's role)

- **PUT** api/profile <br/>
Update personal profile information <br/>
*Returns:* Code 200 on success <br/>
*Query Params:* <br/>
&nbsp;&nbsp;1\) *groupId*, group id value to be linked with current user if it is of STUDENT role (ignore otherwise)<br/>
**TBD**

- **PUT** api/schedule/{date} <br/>
Get schedule for current user and given date <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/ScheduleEntry.java objects <br/>
*Path Params:* <br/>
&nbsp;&nbsp;1\) *date*, in 'yyyy-MM-dd' format<br/>




### User-independent Data Access Endpoints
- **GET** api/teachers <br/>
Get list of teachers that are registered in the system. <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/Teacher.java objects

- **GET** api/subjects <br/>
Get list of subjects that are registered in the system. <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/Subject.java objects


- **GET** api/groups <br/>
Get list of groups that are registered in the system.<br/> 
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/Group.java objects


- **GET**  api/classrooms <br/>
Get list of classrooms that are registered in the system.<br/> 
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/Classroom.java objects


- **GET** api/groups/{group_id}/schedule/{date} <br/>
Get schedule for given GROUP and date. <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/ScheduleEntry.java objects <br/>
*Path Params:* <br/>
&nbsp;&nbsp;1\) Group, as "internal" group id value, not number (i.e. NOT 'TI-154');<br/>
&nbsp;&nbsp;2\) Date, in "yyyy-MM-dd" format <br/>


- **GET** api/teachers/{teacher_id}/schedule/{date} <br/>
Get schedule for given TEACHER and date. <br/>
*Returns:* list of dbdataaccess-pojos/src/main/java/pojos/ScheduleEntry.java objects <br/>
*Path Params:* <br/>
&nbsp;&nbsp;1\) Teacher, by ID<br/>
&nbsp;&nbsp;2\) Date, in "yyyy-MM-dd" format <br/>


- **GET** api/groups/{group_id}/schedule [**TBD**]<br/>
Get entire schedule for given group for current semester <br/>


- **GET** api/teachers/{teacher_id}/schedule [**TBD**]<br/>
Get entire schedule for given teacher for current semester <br/>
