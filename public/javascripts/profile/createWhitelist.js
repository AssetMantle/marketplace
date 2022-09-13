// Date Picker
$(function() {
    $('input[name="startEpoch"]').daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        startDate: moment().startOf('minute'),
        drops:'up',
        locale: {
            format: 'MM/DD/YYYY hh:mm A',
            cancelLabel: 'Clear',
        }
    });

    $('input[name="startEpoch"]').on('apply.daterangepicker', function(ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_START_EPOCH").val(seconds);
    });

    $('input[name="startEpoch"]').on('cancel.daterangepicker', function(ev, picker) {
        $(this).val('');
    });

    function setInitialTime(){
        let dateStr = $('input[name="startEpoch"]').val();
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_START_EPOCH").val(seconds);
    }

    setInitialTime();

    $('input[name="endEpoch"]').daterangepicker({
        autoUpdateInput: false,
        timePicker: true,
        singleDatePicker: true,
        drops:'up',
        locale: {
            cancelLabel: 'Clear',
        }
    });

    $('input[name="endEpoch"]').on('apply.daterangepicker', function(ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const milliseconds = date.getTime();
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_END_EPOCH").val(seconds);
    });

    $('input[name="endEpoch"]').on('cancel.daterangepicker', function(ev, picker) {
        $(this).val('');
    });
});