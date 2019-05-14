select JSON_ARRAYAGG(entry_id)
from fcimapp.schedule
where week_parity is null or week_parity = 'odd'
group by week_day, class_number, classroom_id
having count(1) > 1 and min(main_teacher_id) != max(main_teacher_id)
union
select JSON_ARRAYAGG(entry_id)
from fcimapp.schedule
where week_parity is null or week_parity = 'even'
group by week_day, class_number, classroom_id
having count(1) > 1 and min(main_teacher_id) != max(main_teacher_id)
