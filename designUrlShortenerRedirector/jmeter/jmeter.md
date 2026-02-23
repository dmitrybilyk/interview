rm -rf results_redirect.jtl report_redirect
jmeter -n -t redirect_test.jmx -l results_redirect.jtl -e -o ./report_redirect