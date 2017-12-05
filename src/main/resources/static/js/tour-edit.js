var $hotSpotsList = $("#settings-hotspot-list");
var $sceneList = $("#scene-list-container");
var $centerPoint = $("#center-point");
var $firstSceneBtn = $("#first-scene-btn");
var $saveTourBtn = $("#save-tour-btn");

var tour;
var sceneViewerList = {};
var scenes = {};
var viewer;
var hsViewer;
var minimap;
var markerLayer;
var activeMarkerLayer;

var defaultPreviewSettings = {
    "autoLoad": true,
    "showZoomCtrl": false,
    "keyboardZoom": false,
    "mouseZoom": false,
    "showFullscreenCtrl": false,
    "showControls": false,
    "type": "multires"
};

getTour();

function getTour() {
	$.getJSON(apiUrl + "/public/guest/tours/" + getTourNameFromPath())
		.done(function (data) {
			tour = data;
			switchTour(tour);
		})
}

function getTourNameFromPath() {
	var segments = location.pathname.split('/');
	return segments[segments.length - 1];
}

function setToFirstScene() {
	tour.firstScene = viewer.getScene();
	updateFirstSceneUI();
}

function initMainViewer(tour) {
    if (viewer) {
        viewer.destroy();
        viewer = undefined;
    }

    scenes = {};

    tour.scenes.forEach(function(scene) {
	    scenes[scene.id] = scene;
    });

    if (tour.scenes.length === 0) return;

    var firstScene = tour.firstScene || tour.scenes[0];

    viewer = pannellum.viewer('panorama', {
        "autoLoad": true,

        "default": {
            "firstScene": firstScene,
            "author": "Demo",
            "sceneFadeDuration": 1000
        },

        "scenes": scenes
    });

    viewer.on("load", loadSceneConfig);
    viewer.on("mouseup", updateNorthFace);

    $firstSceneBtn.click(setToFirstScene);
    $saveTourBtn.click(saveTour);

    loadSceneConfig();
}

function tooltipFunc(hotSpotDiv, args) {
    var argsArr = args.split("|");
    var sceneId = argsArr[0];
    var content = argsArr[1];

    var div = document.createElement('div');
    div.classList.add('custom-hotspot-wrapper');
    div.classList.add('custom-tooltip');
    div.classList.add('custom-hotspot-target');

    var span = document.createElement('span');
    span.innerHTML = content;
    hotSpotDiv.appendChild(span);
    hotSpotDiv.appendChild(div);
    span.style.width = span.scrollWidth - 20 + 'px';
    span.style.marginLeft = -(span.scrollWidth - div.offsetWidth) / 2 + 'px';
    span.style.marginTop = -span.scrollHeight - 12 + 'px';
}

function updateFirstSceneUI() {
	if (viewer.getScene() === tour.firstScene) {
		$firstSceneBtn.find("i").text("star");
		$firstSceneBtn.find("span").text("First Scene");
	} else {
		$firstSceneBtn.find("i").text("star_border");
		$firstSceneBtn.find("span").text("Set as first scene");
	}
}

function loadSceneConfig() {

    if (hsViewer) {
        hsViewer.destroy();
        hsViewer = undefined;
    }

	updateFirstSceneUI();
	$hotSpotsList.empty();
    populateSceneList();
    updateNorthFace();

    var hotSpots = getHotSpotsFromView(viewer);

    hotSpots.forEach(function(hotspot) {
        $hotSpotsList
            .append($("<li>").attr('id', "li-" + hotspot.sceneId)
                .click(function() {switchHotSpot(hotspot)})
                .append($("<div>").addClass('collapsible-header').text(hotspot.sceneId))
                .append($("<div>").addClass('collapsible-body')
                    .append(loadHotSpotSettings(hotspot))));
    });

    if (Materialize.updateTextFields) {
	    Materialize.updateTextFields();
    }
}

/**
 return a element
 */
function loadHotSpotSettings(hotspot) {
    return $("<form>")
        .append($("<div>").addClass('row center-align')
            .append($("<a>").addClass("waves-effect waves-light btn")
                .text("Set Pitch & Yaw to Center")
                .append($("<i>").addClass("material-icons left").text("adjust"))
                .click(function(){useCurrentCenter(hotspot)})))
        .append($("<div>").addClass('row')
            .append($("<div>").addClass('input-field col s12')
                .append($("<input>")
                    .addClass('validate')
                    .attr("id", "hotspot-text-" + hotspot.sceneId)
                    .attr("name", "hotspot-text-" + hotspot.sceneId)
                    .attr("type", "text")
                    .val(hotspot.text)
                    .change(function (e) {updateHotSpotValue({text: e.target.value}, hotspot);}))
                .append($("<label>")
                    .attr("for", "hotspot-text-" + hotspot.sceneId)
                    .text("Text"))))
        .append($("<a>").addClass("waves-effect waves-light btn red")
            .text("Delete HotSpot")
            .append($("<i>").addClass("material-icons left").text("delete"))
            .click(function(){removeHotSpot(hotspot);}));
}

function updateHotSpotValue(val, hotspot) {
    viewer.removeHotSpot(hotspot.id);

    var hs = Object.assign(hotspot, val);

    viewer.addHotSpot(hs);

    if (val.hasOwnProperty("targetPitch") || val.hasOwnProperty("targetYaw")) {
        updateHotSpotViewer(hs);
    }
}

function updateHotSpotViewer(hotspot) {
    hsViewer.setPitch(hotspot.targetPitch);
    hsViewer.setYaw(hotspot.targetYaw);
}

function initHotSpotViewer(hotspot) {
    if (hsViewer) {
        hsViewer.destroy();
        hsViewer = undefined;
    }

    var scene = findSceneById(hotspot.sceneId);

    var hsViewerOptions = Object.assign({
        "multiRes": scene.multiRes,
        "yaw": hotspot.targetYaw,
        "pitch": hotspot.targetPitch,
        "id": hotspot.sceneId
    }, defaultPreviewSettings);

    hsViewer = pannellum.viewer('hotspot-pano-container', hsViewerOptions);

    hsViewer.on("mouseup", function () {
        updateHotSpotValue({
            targetPitch: hsViewer.getPitch(),
            targetYaw: hsViewer.getYaw()
        }, hotspot);

        $("#hotspot-targetPitch-" + hotspot.sceneId).val(hsViewer.getPitch());
        $("#hotspot-targetYaw-" + hotspot.sceneId).val(hsViewer.getYaw());
    });
}

function findSceneById(sceneId) {
    var index = Object.keys(scenes).filter(function(key) {
        return key === sceneId;
    })[0];
    return scenes[index];
}

function getHotSpotsFromView(viewer) {
    return viewer.getConfig().hotSpots;
}

function switchHotSpot(hotspot) {
    viewer.setPitch(hotspot.pitch);
    viewer.setYaw(hotspot.yaw);

    if (!hsViewer || hsViewer.getConfig().id !== hotspot.sceneId) {
        initHotSpotViewer(hotspot);
    }
}

function useCurrentCenter(hotspot) {
    updateHotSpotValue({
        pitch: viewer.getPitch(),
        yaw: viewer.getYaw()
    }, hotspot);
}

function populateSceneList() {
    $sceneList.empty();

    var hotSpots = getHotSpotsFromView(viewer);
    var connectedSceneIds = hotSpots.map(function(hs) {
        return hs.sceneId;
    });

    Object.keys(scenes)
        .filter(function(sceneId) {
            return sceneId !== viewer.getScene() && connectedSceneIds.indexOf(sceneId) === -1;
        })
        .forEach(function(sceneId) {
            var id = "scene-preview-" + sceneId;
            var scene = scenes[sceneId];

            $sceneList.append($("<div>").addClass('card')
                .append($("<div>").addClass('card-image')
                    .append($("<div>").attr('id', id).addClass("scene-preview"))
                    .append($("<span>").addClass('card-title').text(scene.title)))
                .append($("<div>").addClass('card-action')
                    .append($("<a>").append($("<i>").addClass("material-icons").text("add_circle_outline"))
                        .click(function() { addToScene(scene);}))
                ));

            var viewerOptions = Object.assign({
                "multiRes": scene.multiRes,
                "id": sceneId
            }, defaultPreviewSettings);

            sceneViewerList[sceneId] = pannellum.viewer(id, viewerOptions);
        });
}

function addToScene(scene) {
    var currentSceneId = viewer.getScene();
    var hs = {
        "id": currentSceneId + "-" + scene.id,
        "pitch": viewer.getPitch(),
        "yaw": viewer.getYaw(),
        "type": "scene",
        "text": scene.id,
        "sceneId": scene.id,
        "targetYaw": scene.yaw,
        "targetPitch": scene.pitch,
        "cssClass": "custom-hotspot",
        "createTooltipFunc": tooltipFunc,
        "createTooltipArgs": scene.id + "|" + scene.id
    };

    viewer.addHotSpot(hs, currentSceneId);

    syncNewHotSpotWithSceneList(hs, currentSceneId);
    populateSceneList();
}

function syncNewHotSpotWithSceneList(newHs, sceneId) {
    loadSceneConfig();

    var currentHotSpotsInScene = scenes[sceneId].hotSpots;
    var hotSpotExists = currentHotSpotsInScene.filter(function(hs) {
        return hs.sceneId === newHs.sceneId;
    }).length > 0;

    if (hotSpotExists) return;

    currentHotSpotsInScene.push(newHs);
}

function syncRemovedHotSpotWithSceneList(removedHs, sceneId) {
    loadSceneConfig();

    var currentHotSpotsInScene = scenes[sceneId].hotSpots;
    scenes[sceneId].hotSpots = currentHotSpotsInScene.filter(function (hs) {
        return hs.id !== removedHs.id;
    });
}

function removeHotSpot(hs) {
    viewer.removeHotSpot(hs.id);
    syncRemovedHotSpotWithSceneList(hs, viewer.getScene());
}

function switchTour(tour) {

    if ($centerPoint.hasClass("hide")) {
        $centerPoint.removeClass("hide");
    }

    addMetaToHotSpots(tour);

    initMainViewer(tour);

    var mapDiv = 'mini-map';
	$("#" + mapDiv).empty();
    if (tour.mapPath) {
        initCustomMiniMap(mapDiv, tour);
    } else {
        initMiniMap(mapDiv);
        initSceneMarkers(tour);
        updateNorthFace();
    }

}

function saveTour() {
    tour.scenes = Object.keys(scenes)
        .map(function (sceneId) {
            return scenes[sceneId];
        });
    tour = removeMetaFromHotSpots(tour);

    $.ajax(apiUrl + "/public/guest/tours/" + tour.name, {
        method: "PUT",
        data: JSON.stringify(tour),
        dataType: "json",
        contentType: "application/json"
    })
        .done(function (res) {
            console.log(res);
        })

}

function removeMetaFromHotSpots(tour) {
    var cleanedTour = JSON.parse(JSON.stringify(tour));
    cleanedTour.scenes.forEach(function(scene) {
        scene.hotSpots.forEach(function(hotspot) {
            delete hotspot.cssClass;
            delete hotspot.createTooltipArgs;
            delete hotspot.div;
        });
    });
    return cleanedTour;
}

function addMetaToHotSpots(tour) {
    tour.scenes.forEach(function(scene) {
        scene.hotSpots.forEach(function(hs) {
            Object.assign(hs, {
                "cssClass": "custom-hotspot",
                "createTooltipFunc": tooltipFunc,
                "createTooltipArgs": scene.id + "|" + scene.id
            });
        });
    });
}

function initMiniMap(mapDiv) {
    minimap = new ol.Map({
        layers: [
            new ol.layer.Tile({
                source: new ol.source.OSM()
            })
        ],
        controls: [
            new ol.control.Attribution({
                collapsible: false
            }),
            new ol.control.Zoom()
        ],
        logo: false,
        target: mapDiv,
        view: new ol.View({
            center: [0, 0],
            zoom: 10
        })
    });
}

function initCustomMiniMap(mapDiv, tour) {
	$("<img/>").attr("src", tour.mapPath).on('load', function(){
		var extent = [0, 0, this.width, this.height];
		var projection = new ol.proj.Projection({
            code: 'custom-map',
            units: 'pixels',
            extent: extent
        });
		var layer = new ol.layer.Image({
            source: new ol.source.ImageStatic({
                url: tour.mapPath,
                projection: projection,
                imageExtent: extent
            })
        });

		minimap = new ol.Map({
			layers: [
				layer
			],
			controls: [
				new ol.control.Attribution({
					collapsible: false
				}),
				new ol.control.Zoom()
			],
			logo: false,
			target: mapDiv,
			view: new ol.View({
				projection: projection,
				center: ol.extent.getCenter(extent),
				zoom: 10
			})
		});

		initSceneMarkers(tour);
		updateNorthFace();
	});

}

function initSceneMarkers(tour) {

    if (markerLayer) {
        minimap.removeLayer(markerLayer);
    }

    var availableScenes = tour.scenes
	    .filter(function(scene) {
	    	if (tour.mapPath) {
	    		return scene.coordinates;
		    }
		    return scene.photoMeta && scene.photoMeta.exif;
	    });
    var markers = availableScenes
	    .map(function(scene) {
		    var pos;

	    	if (tour.mapPath) {
			    var coordinates = scene.coordinates;
			    pos = [coordinates.x, coordinates.y];
		    } else {
			    var exif = scene.photoMeta.exif;
			    pos = ol.proj.fromLonLat([exif.longitude, exif.latitude]);
		    }

		    return new ol.Feature({
			    type: 'icon',
			    geometry: new ol.geom.Point(pos)
		    });
	    });

    markerLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: markers
        }),
        style: new ol.style.Style({
            image: new ol.style.Icon({
                scale: 0.3,
                src: '/img/bullseye.png'
            })
        })
    });

    minimap.addLayer(markerLayer);

    if (availableScenes.length > 0) {
        var coordinates = availableScenes.map(function(scene) {
        	if (tour.mapPath)
        		return [scene.coordinates.x, scene.coordinates.y];

            var exif = scene.photoMeta.exif;
            return [exif.longitude, exif.latitude];
        });

        var boundingExtent = ol.extent.boundingExtent(coordinates);
        if (!tour.mapPath) {
	        boundingExtent = ol.proj.transformExtent(boundingExtent, ol.proj.get('EPSG:4326'), ol.proj.get('EPSG:3857'));
        }
        minimap.getView().fit(boundingExtent, minimap.getSize());
    } else {
    	var ext = minimap.getView().getProjection().getExtent();
    	minimap.getView().fit(ext, minimap.getSize());
    }
}

function updateNorthFace() {
    if (!minimap) return;

	if (activeMarkerLayer) {
		minimap.removeLayer(activeMarkerLayer);
		activeMarkerLayer = undefined;
	}

	var rotationInRadian;
	var viewConfig = viewer.getConfig();

	if (viewConfig.northOffset) {
		rotationInRadian = degreeToRadian(viewConfig.northOffset);
	} else if (viewConfig.photoMeta && viewConfig.photoMeta.exif) {
		var gpano = viewConfig.photoMeta.gpano;

		if (gpano && !isNaN(gpano.poseHeadingDegrees)) {
			viewer.setNorthOffset(-gpano.poseHeadingDegrees);

			var rotation = viewer.getYaw() + gpano.poseHeadingDegrees;
			rotationInRadian = degreeToRadian(rotation);
		}
	}

    if (rotationInRadian && ((tour.mapPath && viewConfig.coordinates) || (viewConfig.photoMeta && viewConfig.photoMeta.exif))) {

	    var pos = tour.mapPath ? (viewConfig.coordinates ?
		    [viewConfig.coordinates.x, viewConfig.coordinates.y] : undefined) :
		    ol.proj.fromLonLat([viewConfig.photoMeta.exif.longitude, viewConfig.photoMeta.exif.latitude]);

	    if (!pos) return;

	    var marker = new ol.Feature({
		    type: 'icon',
		    geometry: new ol.geom.Point(pos)
	    });

	    activeMarkerLayer = new ol.layer.Vector({
		    source: new ol.source.Vector({
			    features: [marker]
		    }),
		    style: new ol.style.Style({
			    image: new ol.style.Icon({
				    scale: 0.6,
				    src: '/img/arrow.png',
				    rotation: rotationInRadian
			    })
		    })
	    });

	    minimap.addLayer(activeMarkerLayer);
    }
}

function degreeToRadian(degree) {
	return degree * Math.PI / 180;
}