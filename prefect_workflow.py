from prefect import Flow, task
from prefect.tasks.shell import ShellTask

# download
download_task = ShellTask(
    name='download',
    command='scripts/ingest/01_download_from_ftp.sh', return_all=True, log_stderr=True)
unzip_task = ShellTask(
    name='unzip',
    command='scripts/ingest/02_extract_downloaded_files.sh', return_all=True, log_stderr=True)
extract_basic_info_task = ShellTask(
    name='extract_basic_info',
    command='scripts/ingest/03_extract_basic_info_from_downloaded_files.sh', return_all=True, log_stderr=True)
import_basic_info_task = ShellTask(
    name='import_basic_info',
    command='scripts/ingest/04_import_basic_info_to_db.sh', return_all=True, log_stderr=True)
harvest_edm_task = ShellTask(
    name='harvest_edm',
    command='scripts/ingest/05_harvest_edm.sh', return_all=True, log_stderr=True)

# measurement tasks
measure_ddb_edm_task = ShellTask(
    name='measure_ddb_edm',
    command='scripts/process/01_process_ddb-edm.sh', return_all=True, log_stderr=True)
measure_marc_task = ShellTask(
    name='measure_marc',
    command='scripts/process/02_process_marc.sh', return_all=True, log_stderr=True)
measure_ddb_dc_task = ShellTask(
    name='measure_dc',
    command='scripts/process/03_process_ddb_dc.sh', return_all=True, log_stderr=True)
measure_lido_task = ShellTask(
    name='measure_lido',
    command='scripts/process/04_process_lido.sh', return_all=True, log_stderr=True)
measure_mets_mods_task = ShellTask(
    name='measure_mets_mods',
    command='scripts/process/05_process_mets-mods.sh', return_all=True, log_stderr=True)

# importing tasks
create_issue_table_task = ShellTask(
    name='create_issue_table',
    command='scripts/process/11_create_issue_table.sqlite.sh', return_all=True, log_stderr=True)
import_ddb_edm_task = ShellTask(
    name='import_ddb_edm',
    command='scripts/process/11_import_ddb-edm.sqlite.sh', return_all=True, log_stderr=True)
import_marc_task = ShellTask(
    name='import_marc',
    command='scripts/process/11_import_marc.sqlite.sh', return_all=True, log_stderr=True)
import_dc_task = ShellTask(
    name='import_dc',
    command='scripts/process/11_import_dc.sqlite.sh', return_all=True, log_stderr=True)
import_lido_task = ShellTask(
    name='import_lido',
    command='scripts/process/11_import_lido.sqlite.sh', return_all=True, log_stderr=True)
import_mets_mods_task = ShellTask(
    name='import_mets_mods',
    command='scripts/process/11_import_mets-mods.sqlite.sh', return_all=True, log_stderr=True)

calculate_aggregations_task = ShellTask(
    name='calculate_aggregations',
    command='scripts/process/12_calculate_aggregations_sqlite.sh', return_all=True, log_stderr=True)

@task
def show_output(std_out):
    print(std_out)


@task
def notify(data):
    for row in data:
        print(row)


with Flow("DDB quality assessment flow") as f:
    # both tasks will be executed in home directory
    # download = download_task()
    # unzip = unzip_task()
    # extract_basic_info = extract_basic_info_task()
    # import_basic_info = import_basic_info_task()
    # harvest_edm = harvest_edm_task()

    # process_ddb_edm = measure_ddb_edm_task()
    # process_marc = measure_marc_task(upstream_dependencies=[process_ddb_edm])
    # process_ddb_dc = measure_ddb_dc_task()
    # process_lido = measure_lido_task()
    # process_mets_mods = process_mets_mods_task()

    # create_issue_table = create_issue_table_task()
    # import_ddb_edm = import_ddb_edm_task()
    # import_marc = import_marc_task()
    # import_dc = import_dc_task()
    # import_lido = import_lido_task()
    import_mets_mods = import_mets_mods_task()
    calculate_aggregations = calculate_aggregations_task(upstream_tasks=[import_mets_mods])

    # notify(harvest_edm)


out = f.run()
