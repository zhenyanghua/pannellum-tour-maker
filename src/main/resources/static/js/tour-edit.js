var $hotSpotsList = $("#settings-hotspot-list");
var $sceneList = $("#scene-list-container");
var $centerPoint = $("#center-point");
var $firstSceneBtn = $("#first-scene-btn");
var $setToNorthBtn = $("#set-north-offset-btn");
var $saveTourBtn = $("#save-tour-btn");

var tour;
var scenes = {};
var viewer;
var hsViewer;
var minimap;
var markerLayer;
var markerGraphLayer;
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

function setToNorth() {
	var northOffset = 0 - viewer.getYaw();
	viewer.setNorthOffset(northOffset);
	scenes[viewer.getScene()].northOffset = northOffset;
	updateNorthFace();
	viewer.loadScene(viewer.getScene(), viewer.getPitch(), viewer.getYaw(), viewer.getHfov());
}

function updateCompass(rotation) {
	var $compass = $("#panorama").find(".pnlm-compass");
	var transform = 'rotate(' + rotation + 'deg)';
	$compass.css('transform', transform);
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
	    "compass": true,

        "default": {
            "firstScene": firstScene,
            "sceneFadeDuration": 400
        },

        "scenes": scenes
    });

    viewer.on("load", loadSceneConfig);
    viewer.on("mouseup", updateNorthFace);

    $firstSceneBtn.click(setToFirstScene);
	$setToNorthBtn.click(setToNorth);
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
            var scene = scenes[sceneId];

            $sceneList.append($("<div>").addClass('card')
                .append($("<div>").addClass('card-image')
                    .append($("<img>").attr('src', scene.multiRes.basePath + "/preview.png").addClass("scene-preview"))
                    .append($("<span>").addClass('card-title').text(scene.title)))
                .append($("<div>").addClass('card-action')
                    .append($("<a>").append($("<i>").addClass("material-icons").text("add_circle_outline"))
                        .click(function() { addToScene(scene);}))
                ));
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

    var pos = getScenePosition(scenes[currentSceneId]);
    if (pos) {
	    handleDrawMarker(pos)
    }
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

function removeHotSpot(hotspot) {
    viewer.removeHotSpot(hotspot.id);
    syncRemovedHotSpotWithSceneList(hotspot, viewer.getScene());

	var markerGraphSource = markerGraphLayer.getSource();
	markerGraphSource.getFeatures()
		.filter(function(feature) {
			return feature.getId() === hotspot.id;
		})
		.forEach(function(feature) {
			markerGraphSource.removeFeature(feature);
		});
}

function switchTour(tour) {

    if ($centerPoint.hasClass("hide")) {
        $centerPoint.removeClass("hide");
    }

    addMetaToHotSpots(tour);

    initMainViewer(tour);

    var mapDiv = 'mini-map';

	$("#" + mapDiv).empty();

	ol.inherits(PlaceMarkerControl, ol.control.Control);

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
            new ol.control.Zoom(),
	        new PlaceMarkerControl()
        ],
        logo: false,
        target: mapDiv,
        view: new ol.View({
            center: [0, 0],
            zoom: 10,
	        maxZoom: 21
        })
    });
	minimap.on('click', onMapClick);
}

function handleDrawMarker(coordinate) {
	$('.place-marker').removeClass("active");

	var scene = scenes[viewer.getScene()];

	if (tour.mapPath) {
		if (!scene.coordinates) scene.coordinates = {};

		scene.coordinates.x = coordinate[0];
		scene.coordinates.y = coordinate[1];
	} else {
		if (!scene.photoMeta) {
			scene.photoMeta = {};
			if (!scene.photoMeta.exif) {
				scene.photoMeta.exif = {};
			}
		}
		var longLat = ol.proj.transform(coordinate, ol.proj.get('EPSG:3857'), ol.proj.get('EPSG:4326'));
		scene.photoMeta.exif.longitude = longLat[0];
		scene.photoMeta.exif.latitude = longLat[1];
	}
	var markerSource = markerLayer.getSource();
	markerSource.getFeatures()
		.filter(function(feature) {
			return feature.getId() === scene.id; })
		.forEach(function(feature) {
			markerSource.removeFeature(feature); });

	var marker = new ol.Feature({
		type: 'icon',
		geometry: new ol.geom.Point(coordinate)
	});
	marker.setId(scene.id);
	markerSource.addFeature(marker);

	var markerGraphSource = markerGraphLayer.getSource();
	markerGraphSource.getFeatures()
		.filter(function(feature) {
			return new RegExp(scene.id, 'i').test(feature.getId());
		})
		.forEach(function(feature) {
			markerGraphSource.removeFeature(feature);
		});
	// search in all hotspots related to this scene.
	scene.hotSpots.forEach(function(hotspot) {
		var targetScene = scenes[hotspot.sceneId];
		var dest = getScenePosition(targetScene);
		if (dest) {
			var line = new ol.Feature(new ol.geom.LineString([coordinate, dest]));
			line.setId(hotspot.id);
			markerGraphSource.addFeature(line);
		}
	});

	// search in all scenes for related hotspots
	Object.keys(scenes).forEach(function(sceneId) {
		var targetScene = scenes[sceneId];
		targetScene.hotSpots
			.filter(function(hotpspot) {
				return hotpspot.sceneId === scene.id;
			})
			.forEach(function(hotspot) {
				var origin = getScenePosition(targetScene);
				var dest = getScenePosition(scenes[hotspot.sceneId]);
				if (origin && dest) {
					var line = new ol.Feature(new ol.geom.LineString([origin, dest]));
					line.setId(hotspot.id);
					markerGraphSource.addFeature(line);
				}
			})
	});

	updateNorthFace();
}

function handleClickMarker(pixel) {
	minimap.forEachFeatureAtPixel(pixel, function(feature) {
		if (!feature || !scenes[feature.getId()]) return;
		viewer.loadScene(feature.getId());
	});
}

function onMapClick(e) {
	var $placeMarker = $('.place-marker');

	if ($placeMarker.hasClass('active')) {
		handleDrawMarker(e.coordinate);
	} else {
		handleClickMarker(e.pixel);
	}
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
				new ol.control.Zoom(),
				new PlaceMarkerControl()
			],
			logo: false,
			target: mapDiv,
			view: new ol.View({
				projection: projection,
				center: ol.extent.getCenter(extent),
				zoom: 10,
				maxZoom: 3
			})
		});

		minimap.on('click', onMapClick);

		initSceneMarkers(tour);
		updateNorthFace();
	});

}

function initSceneMarkers(tour) {

    if (markerLayer) {
        minimap.removeLayer(markerLayer);
    }
    if (markerGraphLayer) {
    	minimap.removeLayer(markerGraphLayer);
    }

	var markers = [];
	var lines = [];
    var scenesWithGeometry = tour.scenes
	    .filter(function(scene) {
	    	if (tour.mapPath) {
	    		return scene.coordinates;
		    }
		    return scene.photoMeta && scene.photoMeta.exif;
	    });

	scenesWithGeometry
	    .forEach(function(scene) {
		    var pos;

	    	if (tour.mapPath) {
			    var coordinates = scene.coordinates;
			    pos = [coordinates.x, coordinates.y];
		    } else {
			    var exif = scene.photoMeta.exif;
			    pos = ol.proj.fromLonLat([exif.longitude, exif.latitude]);
		    }

		    var marker = new ol.Feature(new ol.geom.Point(pos));
	    	marker.setId(scene.id);
		    markers.push(marker);

		    scene.hotSpots
			    .forEach(function(hotspot) {
			    	var scene = scenes[hotspot.sceneId];
			    	var dest = getScenePosition(scene);
			    	if (dest) {
					    var line = new ol.Feature(new ol.geom.LineString([pos, dest]));
					    line.setId(hotspot.id);
					    lines.push(line);
				    }
			    })
	    });

    markerLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: markers
        }),
        style: new ol.style.Style({
            image: new ol.style.Icon({
                scale: 0.24,
                src: '/img/bullseye.png'
            })
        })
    });

    minimap.addLayer(markerLayer);

	var styleFunction = function(feature) {
		var geometry = feature.getGeometry();
		var styles = [
			// linestring
			new ol.style.Style({
				stroke: new ol.style.Stroke({
					width: 3,
					color: [255, 0, 0, 0.4],
					lineDash: [.2, 5]
				})
			})
		];

		geometry.forEachSegment(function(start, end) {
			var dx = end[0] - start[0];
			var dy = end[1] - start[1];
			var rotation = Math.atan2(dy, dx);
			// arrows
			styles.push(new ol.style.Style({
				geometry: new ol.geom.Point(end),
				image: new ol.style.Icon({
					src: '../img/thin-arrow-red.png',
					anchor: [1.3, 0.5],
					rotateWithView: true,
					rotation: -rotation
				})
			}));
		});

		return styles;
	};

    markerGraphLayer = new ol.layer.Vector({
	    source: new ol.source.Vector({
		    features: lines
	    }),
	    style: styleFunction
    });

    minimap.addLayer(markerGraphLayer);

	var hoverInteraction = new ol.interaction.Select({
		condition: ol.events.condition.pointerMove,
		layers:[markerLayer],
		style: new ol.style.Style({
			image: new ol.style.Icon({
				scale: 0.3,
				src: '/img/bullseye.png'
			})
		})
	});
	minimap.addInteraction(hoverInteraction);

    if (scenesWithGeometry.length > 0 && !tour.mapPath) {
        var coordinates = scenesWithGeometry.map(function(scene) {
        	if (tour.mapPath)
        		return [scene.coordinates.x, scene.coordinates.y];

            var exif = scene.photoMeta.exif;
            return [exif.longitude, exif.latitude];
        });

        var boundingExtent = ol.extent.boundingExtent(coordinates);
	    boundingExtent = ol.proj.transformExtent(boundingExtent, ol.proj.get('EPSG:4326'), ol.proj.get('EPSG:3857'));
        minimap.getView().fit(boundingExtent, minimap.getSize());
    } else {
    	var ext = minimap.getView().getProjection().getExtent();
    	minimap.getView().fit(ext, minimap.getSize());
    }
}

function getScenePosition(scene) {
	var pos;
	if (tour.mapPath && scene.coordinates) {
		pos = [scene.coordinates.x, scene.coordinates.y];
	} else if (!tour.mapPath && scene.photoMeta && scene.photoMeta.exif) {
		pos = ol.proj.fromLonLat([scene.photoMeta.exif.longitude, scene.photoMeta.exif.latitude]);
	}
	return pos;
}

function updateNorthFace() {

	var scene = scenes[viewer.getScene()];
	var rotationInRadian;
	var compassRotate;

	if (!tour.mapPath && (scene.photoMeta && scene.photoMeta.gpano && !isNaN(scene.photoMeta.gpano.poseHeadingDegrees))) {
		var gpano = scene.photoMeta.gpano;
		viewer.setNorthOffset(-gpano.poseHeadingDegrees);
		compassRotate = viewer.getYaw() + gpano.poseHeadingDegrees;
		updateCompass(compassRotate);
		rotationInRadian = degreeToRadian(compassRotate);
	} else {
		compassRotate = viewer.getYaw() + scene.northOffset;
		updateCompass(compassRotate);
		rotationInRadian = degreeToRadian(compassRotate);
	}

	if (!minimap) return;

	if (activeMarkerLayer) {
		minimap.removeLayer(activeMarkerLayer);
		activeMarkerLayer = undefined;
	}

	var pos = getScenePosition(scene);

	if (pos) {
		var marker = new ol.Feature({
			type: 'icon',
			geometry: new ol.geom.Point(pos)
		});
		marker.setId(viewer.getScene());

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

function PlaceMarkerControl(opt_options) {

	var options = opt_options || {};

	var button = document.createElement('button');
	button.innerHTML = '<i class="material-icons">pin_drop</i>';

	var this_ = this;

	button.addEventListener('click', handlePlaceMarker, false);
	button.addEventListener('touchstart', handlePlaceMarker, false);

	var element = document.createElement('div');
	element.className = 'place-marker ol-unselectable ol-control';
	element.appendChild(button);

	ol.control.Control.call(this, {
		element: element,
		target: options.target
	});

	function handlePlaceMarker() {
		element.classList.add("active");
	}

}
