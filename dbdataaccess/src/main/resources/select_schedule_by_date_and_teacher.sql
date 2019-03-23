select
	'regular' as class_type, sch.schedule_entry_id, sch.cancelled_flg
	, c.date_key as `date`, class_time_interval
	, classroom_name
	, group_number, subgroup
	, teacher_full_name
	, subject_type, subject_type_abbreviated
	, subject_name, subject_name_abbreviated
	, subject_id, group_id, teacher_id
from fcimapp.calendar c
inner join fcimapp.vw_denormalized_regular_schedule sch
	on c.week_day_number = sch.week_day
where 
	(sch.week_parity is null or c.week_parity = sch.week_parity)
	and sch.teacher_id = ?
	and c.date_key = ?
union all
select 
	'dated' as class_type, schedule_entry_id, 0 as cancelled_flg
	, date_key as `date`, class_time_interval
	, classroom_name
	, group_number, subgroup
	, teacher_full_name
	, subject_type, subject_type_abbreviated
	, subject_name, subject_name_abbreviated
	, subject_id, group_id, teacher_id
from fcimapp.vw_denormalized_dated_schedule schd
where 
	schd.teacher_id = ?
	and schd.date_key = ?