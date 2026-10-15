with open("index.html", "r", encoding="utf-8") as f:
    html = f.read()

# Extract the weather banner HTML
banner_match = html.find('<!-- DYNAMIC GPS LOCATION & LIVE WEATHER TELEMETRY BANNER -->')
if banner_match != -1:
    # Find the end of the banner div
    # Let's find where page-dashboard starts
    dash_match = html.find('<section class="page-view active" id="page-dashboard">')
    if dash_match != -1:
        # Let's clean up and move banner inside page-dashboard
        # First remove banner from current position
        # Let's find the banner block
        end_banner = html.find('</div>\n            </div>\n\n                <div class="stats-grid">', banner_match)
        if end_banner == -1:
            end_banner = html.find('</div>\n            </div>\n\n            <div class="stats-grid">', banner_match)
        
        if end_banner != -1:
            banner_html = html[banner_match:end_banner + len('</div>\n            </div>')]
            # Remove old position
            html = html.replace(banner_html, '')
            
            # Insert right inside page-dashboard
            target_dash = '<section class="page-view active" id="page-dashboard">'
            html = html.replace(target_dash, target_dash + '\n            ' + banner_html + '\n')
            print("Successfully moved GPS weather banner inside page-dashboard!")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(html)

print("index.html updated successfully!")
