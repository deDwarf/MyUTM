SELECT s.*, g.group_name as group_number
FROM fcimapp.students s
inner join fcimapp.accounts a
  on s.account_id = a.account_id
inner join fcimapp.`groups` g
  on s.group_id = g.group_id
where a.user_login = ?
