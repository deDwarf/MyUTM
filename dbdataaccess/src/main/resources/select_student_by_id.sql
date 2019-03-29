SELECT s.*, g.group_name as group_number
FROM fcimapp.students s
inner join fcimapp.`groups` g
  on s.group_id = g.group_id
where s.student_id = ?
